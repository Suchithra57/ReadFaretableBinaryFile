package com.readbin.main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import com.genfare.cloud.device.common.TicketType;
import com.genfare.cloud.device.common.ValidDayType;
import com.genfare.cloud.device.fare.BadListedCardType;
import com.genfare.cloud.device.fare.FareCellType;
import com.genfare.cloud.device.fare.FareSetType;
import com.genfare.cloud.device.fare.FareTableAPIType;
import com.genfare.cloud.device.fare.HolidayType;


public class MainApplication {

	//static File file = new File("E:\\dllv5i.bin");
	 static File file =new File("C:\\Users\\Suchithra\\Videos\\dllv5i.bin");

	public static void main(String[] args) throws Exception {

		try {

			FileInputStream fileInputStream = new FileInputStream(file);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			byte[] data = new byte[46502];

			int datacount = fileInputStream.read(data);
			while (datacount != -1) {
				dos.write(data, 0, datacount);
				datacount = fileInputStream.read(data);
			}

			byte b1[] = baos.toByteArray();
			convertBin(b1);
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}

	private static void convertBin(byte[] bin)
	{
		FaretableBinConversion  faretableBin= new FaretableBinConversion();
		FareTableAPIType reverseFareTable=faretableBin.reverseConversion(bin);
		List<FaretableTransferDTO> transferList=faretableBin.transferControl(bin);
		
		System.out.println("\n FareStructure Information \n");
		System.out.println("Fare Structure Effective Date/Time: "+reverseFareTable.getFareStructures().getFareStructure().get(0).getEffectiveDate().getValue());
	
		System.out.println("AltKey :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getAltKey());
		System.out.println("PeakTime1Start :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getPeakTime1Start());
		System.out.println("PeakTime1End :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getPeakTime1End());
		System.out.println("PeakTime2Start :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getPeakTime2Start());
		System.out.println("PeakTime2End :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getPeakTime2End());
		System.out.println("LockCode :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getLockCodeValue());
		System.out.println("LockCodeType :"+reverseFareTable.getFareStructures().getFareStructure().get(0).getLockCode().getId());
			
//		FareSets
		List<FareSetType> faresets=reverseFareTable.getFareStructures().getFareStructure().get(0).getFareSets().getFareSet();
		System.out.println("Faresets size :"+faresets.size());
		for(int i=0;i<faresets.size();i++)
		{
			System.out.println("\n FARE SET #"+(i+1));
			
			List<FareCellType> farecells=faresets.get(i).getFareCells().getFareCell();
			
			Formatter formatter = new Formatter();
		    System.out.println(formatter.format("%10s    |  %10s  |   %10s  |   %10s  |   %10s  |  %10s  |  %10s|  %10s", 
		    		"TTP","Value", "Sound1", "Sound2", "LLight","RLight","TrNdx","Attribute"));
		    
		    System.out.println("--------------------------------------------------------------------------------------------------------------------");
			
			for(int j=0;j<farecells.size();j++)
			{
				formatter = new Formatter();
				FareCellType cell = farecells.get(j);
				String ttp="";
				if(j==0)
					ttp="FS";
				if(j==1)
					ttp="Preset";
				if(j>=2 && j<16)
					ttp="KEY"+(j-1);
				if(j>=16 && j<=farecells.size())
					ttp="TTP"+(j-15);
			
				System.out.println(formatter.format("%10s    |  %10s  |   %10s  |   %10s  |   %10s  |  %10s  |  %10s|  %10s",ttp,cell.getValue(),cell.getPreSound().getCode(),cell.getPostSound().getCode(),
						cell.getLed1().getCode(),cell.getLed2().getCode(),cell.getDocument().getTicketId(),cell.getAttribute1().getId()));
			}
			if(i==1)
				break;
		}
		
//		MediaList
		
		List<TicketType> tickets = reverseFareTable.getTickets().getTicket();
		System.out.println("\n MediaList");
		Formatter formatter = new Formatter();
	    System.out.println(formatter.format("%10s    |  %10s  |   %10s  |   %10s  |   %10s","ID","Group","Designator","Text","Valid Days")); 
		System.out.println("------------------------------------------------------------------------------------");
		
		for(int t=0;t<tickets.size();t++)
		{
			TicketType ticket = tickets.get(t);
			formatter = new Formatter();
			List<String> validDays = new ArrayList<String>();
			for(ValidDayType days:ticket.getValidDays().getValidDay())
			{
				validDays.add(days.getDescription());
			}
			
			 System.out.println(formatter.format("%10s    |  %10s  |   %10s  |   %10s  |   %10s",(t+1),ticket.getGroup(),ticket.getDesignator(),ticket.getFareboxConfiguration().getDisplayText(),validDays)); 
		
		}
		
		
		
//		Holiday 
		List<HolidayType> holidays = reverseFareTable.getHolidays().getHoliday();
		System.out.println("\n Holiday \n");
		
		Formatter formatter1 = new Formatter();
	    System.out.println(formatter1.format("%10s    |  %10s  |   %10s|  %10s","ID","Control","Date","Month")); 
		System.out.println("------------------------------------------------------------");
		for(int h=0;h<holidays.size();h++)
		{
			formatter1 = new Formatter();
			if(holidays.get(h).getHolidayDate()!=null)
			{
				System.out.println(formatter1.format("%10s    |  %10s  |   %10s|  %10s",(h+1),15,holidays.get(h).getHolidayDate().getValue().getDay(),holidays.get(h).getHolidayDate().getValue().getMonth()));
			}
			else {
				System.out.println(formatter1.format("%10s    |  %10s  |   %10s|  %10s",(h+1),15,0,0));
				
			}
			
		}
		
		
		
//		Transfer Control
		System.out.println("\n Transfer Control \n");
		Formatter formatter2 = new Formatter();
	    System.out.println(formatter2.format("%5s    |  %5s  |   %5s  |  %5s  |  %5s  |  %6s  |  %15s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s",
	    		"ID","Group","Desig","Trips","Exp Res","Exp Off","Text", "ODDR","ODSR","SDDR","SDSR","cap","hold","payreq","upgred")); 
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for(int a=0;a<transferList.size();a++)
		{
			formatter2 = new Formatter();
			System.out.println(formatter2.format("%5s    |  %5s  |   %5s  |  %5s  |  %6s  |  %6s  |  %17s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s  |  %6s",
					transferList.get(a).getTransferIndex(),transferList.get(a).getGroup(),transferList.get(a).getDesignator(),transferList.get(a).getTrips()
					,transferList.get(a).getExpResolution(),transferList.get(a).getExpOffset(),transferList.get(a).getText(),transferList.get(a).getOppDirDifRoute(),
					transferList.get(a).getOppDirSmRoute(),transferList.get(a).getSameDirDifRoute(),transferList.get(a).getSameDirSmRoute(),transferList.get(a).getCapFlag()
					,transferList.get(a).getHoldFlag(),transferList.get(a).getPayreqFlag(),transferList.get(a).getUpgredFlag()));
		}
		
		
//		BADLIST RANGE
		
		System.out.println("\n BADLIST RANGE ");
		Formatter formatter3 = new Formatter();
	    System.out.println(formatter3.format("%5s    |  %5s  |   %5s  |  %5s  |  %5s  |  %5s  |  %5s  |  %10s  |  %10s",
	    		"ID","Group","Desig","Sec.Code","Agency","MID","TBPC","Start", "Stop")); 
		
		System.out.println("--------------------------------------------------------------------------------------------------------");
		List<BadListedCardType> badlists = reverseFareTable.getBadListedCards().getBadListedCard();
		for(int b=0;b<badlists.size();b++)
		{
			formatter3 = new Formatter();
			 System.out.println(formatter3.format("%5s    |  %5s  |   %5s  |  %7s  |  %7s  |  %5s  |  %5s  |  %10s  |  %10s",
					 (b+1),	badlists.get(b).getGroup(),	badlists.get(b).getDesignator(),badlists.get(b).getSecurityCode(),badlists.get(b).getAgencyID(),
					 badlists.get(b).getManufactureID(),badlists.get(b).getThirdPartyNumber(),badlists.get(b).getSequence1(),badlists.get(b).getSequence2()));
			
		}
	}

}
