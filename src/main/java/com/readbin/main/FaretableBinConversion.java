package com.readbin.main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.genfare.cloud.device.common.DateType;
import com.genfare.cloud.device.common.FareBoxAttributeType;
import com.genfare.cloud.device.common.FareBoxLEDType;
import com.genfare.cloud.device.common.FareBoxSoundType;
import com.genfare.cloud.device.common.FareboxConfigurationType;
import com.genfare.cloud.device.common.TicketType;
import com.genfare.cloud.device.common.ValidDayType;
import com.genfare.cloud.device.common.ValidDaysType;
import com.genfare.cloud.device.fare.BadListedCardType;
import com.genfare.cloud.device.fare.FareCellType;
import com.genfare.cloud.device.fare.FareSetType;
import com.genfare.cloud.device.fare.FareStructureLockCodeType;
import com.genfare.cloud.device.fare.FareStructureType;
import com.genfare.cloud.device.fare.FareTableAPIType;
import com.genfare.cloud.device.fare.HolidayType;

public class FaretableBinConversion {
	FareTableAPIType faretable = new FareTableAPIType();

	public FareTableAPIType reverseConversion(byte[] binStream) {

		getReverseFareStructuresData(binStream);

		getReverseMediaList(binStream);

		transferControl(binStream);

		getHolidayList(binStream);

		getBadListedCards(binStream);
		
		//getEffectiveDate(binStream);
		
		//getCrc16Data(binStream);

		return faretable;
	}
	public List<FareStructureType> getReverseFareStructuresData(byte[] binStream) {
		faretable.setFareStructures(new FareTableAPIType.FareStructures());
		List<FareStructureType> fareStructureList = faretable.getFareStructures().getFareStructure();

		FareStructureType fs = new FareStructureType();
		String loc = "", altkey = "", lockcode = "";

//		fs.setId(new BigInteger("1"));
		fs.setEffectiveDate(getEffectiveDate(binStream));
		
		String peakTime1Start_hour = "", peakTime1Start_min = "", peakTime1End_hour = "", peakTime1End_min = "",
				peakTime2Start_hour = "", peakTime2Start_min = "", peakTime2End_hour = "", peakTime2End_min = "";
		for (int i = 0; i < 18; i++) {
			switch (i) {
			case 0:
			case 1:
				loc = loc + (char) binStream[i];
				break;
			case 2:
				altkey = altkey + (char) binStream[i];
				fs.setAltKey(altkey);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				lockcode = lockcode + (char) binStream[i];
				break;
			case 8:
				FareStructureLockCodeType lockCodeType = new FareStructureLockCodeType();
				lockCodeType.setId(BigInteger.valueOf(Integer.parseInt(String.format("%02X", binStream[i]), 16)));
				fs.setLockCode(lockCodeType);
				
				break;
			case 9: // hardcoded value
				break;
			case 10:
				peakTime1Start_hour = peakTime(binStream[i]);
				break;
			case 11:
				peakTime1Start_min = peakTime(binStream[i]);
				break;
			case 12:
				peakTime1End_hour = peakTime(binStream[i]);
				break;
			case 13:
				peakTime1End_min = peakTime(binStream[i]);
				break;
			case 14:
				peakTime2Start_hour = peakTime(binStream[i]);
				break;
			case 15:
				peakTime2Start_min = peakTime(binStream[i]);
				break;
			case 16:
				peakTime2End_hour = peakTime(binStream[i]);
				break;
			case 17:
				peakTime2End_min = peakTime(binStream[i]);
				break;
			}
		}

		// fs.setLocation(loc);
		fs.setLockCodeValue(lockcode);
		fs.setPeakTime1Start(peakTime1Start_hour + ":" + peakTime1Start_min);
		fs.setPeakTime1End(peakTime1End_hour + ":" + peakTime1End_min);
		fs.setPeakTime2Start(peakTime2Start_hour + ":" + peakTime2Start_min);
		fs.setPeakTime2End(peakTime2End_hour + ":" + peakTime2End_min);

		// FareSets info
		fs.setFareSets(new FareStructureType.FareSets());
		List<FareSetType> FareSetsList = fs.getFareSets().getFareSet();

		long fareset_start = 18, fareset_end = 41140;
		for (int i = 0; i < 20; i++) {
			FareSetType fareSetType = new FareSetType();

			fareSetType.setId(BigInteger.valueOf(i));

			fareSetType.setFareCells(new FareSetType.FareCells());
			List<FareCellType> FareCellTypeList = fareSetType.getFareCells().getFareCell();

			for (int j = 0; j < 257; j++) {

				FareCellType fareCellType = new FareCellType();
				int f = 0;
				int attr=0;
				String value6 = "", value7 = "";
				float value = 0;
				for (int k = (int) fareset_start; k < (fareset_start + 8); k++) { // row wise iterate

					switch (f) {
					case 0:
						fareCellType.setPreSound(getPreSound(binStream[k]));
						fareCellType.setPostSound(getPostSound(binStream[k]));
						break;
					case 1:
						fareCellType.setLed1(getLeftLed(binStream[k]));
						fareCellType.setLed2(getRightLed(binStream[k]));
						break;
					case 2:
						TicketType tickettype = new TicketType();
						tickettype.setTicketId(Integer.parseInt(String.format("%02X", binStream[k]), 16));
						fareCellType.setDocument(tickettype);
						break;
					case 3:// spare byte
						break;
					case 4:
					case 5:
						attr += Integer.parseInt(String.format("%02X", binStream[k]), 16);
						if (j == 0 && attr == 0) {
							fareSetType.setEnabled("T");
						}
						if(j==0 && attr==255)
						{
							fareSetType.setEnabled("F");
						}
						break;
					case 6:
						value6 = String.format("%02X", binStream[k]);
						break;
					case 7:
						value7 = String.format("%02X", binStream[k]);
						break;
					case 8:
						f = 0;
						break;
					}
					f++;
				}
				value = Integer.parseInt(value7 + value6, 16);
				fareCellType.setValue(value);
				FareBoxAttributeType Attr1 = new FareBoxAttributeType();
				Attr1.setId(BigInteger.valueOf(attr));
				fareCellType.setAttribute1(Attr1);
//				if (j != 0) 
					FareCellTypeList.add(fareCellType);
				
				fareset_start += 8;
				if (fareset_start == fareset_end) 
					break;
			}
			FareSetsList.add(fareSetType);
			fareStructureList.add(fs);
		}
		return fareStructureList;
	}

	public String peakTime(byte binStream) {
		String peakTimeData = "";
		long peakTimeInfo = Integer.parseInt(String.format("%02X", binStream), 16);
		if (peakTimeInfo == 0) {
			peakTimeData = "00";
		} else if (peakTimeInfo > 0 && peakTimeInfo < 10) {
			peakTimeData = "0" + peakTimeInfo;
		} else {
			peakTimeData = peakTimeInfo + "";
		}
		return peakTimeData;

	}

	public FareBoxSoundType getPreSound(byte binStream) {
		FareBoxSoundType preSound = new FareBoxSoundType();
		preSound.setId(null);
		preSound.setCode((Integer.parseInt(String.format("%02X", binStream), 16) / 16) + "");
		preSound.setDescription(null);
		return preSound;

	}

	public FareBoxSoundType getPostSound(byte binStream) {
		FareBoxSoundType postSound = new FareBoxSoundType();
		postSound.setId(null);
		postSound.setCode((Integer.parseInt(String.format("%02X", binStream), 16) % 16) + "");
		postSound.setDescription(null);
		return postSound;

	}

	public FareBoxLEDType getRightLed(byte binStream) {
		FareBoxLEDType RightLed = new FareBoxLEDType();
		RightLed.setId(null);
		RightLed.setCode(Integer.parseInt(String.format("%02X", binStream), 16) % 16 + "");
		RightLed.setDescription(null);
		return RightLed;
	}

	public FareBoxLEDType getLeftLed(byte binStream) {
		FareBoxLEDType LeftLed = new FareBoxLEDType();
		LeftLed.setId(null);
		LeftLed.setCode(Integer.parseInt(String.format("%02X", binStream), 16) / 16 + "");
		LeftLed.setDescription(null);
		return LeftLed;

	}

	public List<TicketType> getReverseMediaList(byte[] binStream) {
		// media list info
		long startposition = 41140;
		long endposition = 44033;

		faretable.setTickets(new FareTableAPIType.Tickets());
		List<TicketType> ticketTypeList = faretable.getTickets().getTicket();

		for (int x = 1; x <= 241; x++) {
			TicketType ticket = new TicketType();

			ticket.setTicketId(x);
			String display_text = "";
			int flag, r = 0;

			for (int y = (int) startposition; y < (startposition + 12); y++) {

				switch (r) {

				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					display_text = display_text + (char) binStream[y];
					break;
				case 8:
					ticket.setDesignator((Integer.parseInt(String.format("%02X", binStream[y]), 16)));
					break;
				case 9:
					ticket.setGroup((byte) Integer.parseInt(String.format("%02X", binStream[y]), 16));
					break;
				case 10:
					flag = Integer.parseInt(String.format("%02X", binStream[y]), 16);

					ticket.setValidDays(new ValidDaysType());
					List<ValidDayType> validList = ticket.getValidDays().getValidDay();
					getValidDays(validList, flag);
					break;
				case 11:
					ticket.setValue(BigDecimal.valueOf(Integer.parseInt(String.format("%02X", binStream[y]), 16)));
					break;
				case 12:
					r = 0;
					break;
				}
				r++;
			}
			FareboxConfigurationType fareboxConfig = new FareboxConfigurationType();
			fareboxConfig.setDisplayText(display_text);
			ticket.setFareboxConfiguration(fareboxConfig);

			ticketTypeList.add(ticket);
			startposition += 12;

			if (startposition == endposition) {
				break;
			}
		}
		return ticketTypeList;
	}

	public List<ValidDayType> getValidDays(List<ValidDayType> validList, int flag) {
		Integer[] arr = { 1, 2, 4, 8, 16, 32, 64 };
		Vector<Integer> A = new Vector<>(Arrays.asList(arr));
		ArrayList<Integer> flagList = new ArrayList<>();
		flagList = Combination(A, flag);

		for (int a = 0; a < flagList.size(); a++) {
			ValidDayType validDay = new ValidDayType();
			if (flagList.get(a) == 1) {
				validDay.setDescription("Peak");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}
			if (flagList.get(a) == 2) {
				validDay.setDescription("Off Peak");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}
			if (flagList.get(a) == 4) {
				validDay.setDescription("Week Days");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}
			if (flagList.get(a) == 8 || flagList.get(a) == 16) {
				validDay.setDescription("Weekend");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}
			if (flagList.get(a) == 32) {
				validDay.setDescription("Holidays");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}
			if (flagList.get(a) == 64) {
				validDay.setDescription("Friendly");
				validDay.setIsDate(null);
				validDay.setValidDay("" + flagList.get(a));
			}

			validList.add(validDay);
		}
		return validList;
	}

	public List<FaretableTransferDTO> transferControl(byte[] binStream) {
		// Transfer Control 63 rows
		List<FaretableTransferDTO> transferList = new ArrayList<>();

		long start = 44052, end = 45440;
		for (int x = 1; x <= 63; x++) {
			
			FaretableTransferDTO transfer = new FaretableTransferDTO();
			transfer.setTransferIndex(x);
			String displayText = "";
			int flag, expoff = 0, r = 0;
			for (int y = (int) start; y < (start + 22); y++) {
				switch(r) {
				case 0:
					transfer.setDesignator(Integer.parseInt(String.format("%02X", binStream[y]), 16));
					break;
				case 1:
					transfer.setGroup(Integer.parseInt(String.format("%02X", binStream[y]), 16));
					break;
				case 2:
					flag = Integer.parseInt(String.format("%02X", binStream[y]), 16);
					getAcceptanceRule(transfer, flag);
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
					displayText = displayText + (char) binStream[y];
					break;
				case 19://hard coded value
					break;
				case 20:
					getTripsAndExpResAndExpOff(transfer, binStream[y],expoff);
					break;
				case 21:
					transfer.setExpOffset(BigInteger.valueOf(expoff + 
							Integer.parseInt(String.format("%02X", binStream[y]), 16)));
					break;
				case 22:
					r = 0;
					break;
				}
				r++;
			}
			transfer.setText(displayText);

			transferList.add(transfer);
			start += 22;
			if (start == end) {
				break;
			}
		}
		return transferList;

	}
	
	public void getAcceptanceRule(FaretableTransferDTO transfer, int flag)
	{
		Integer[] arr = { 1, 2, 4, 8, 16, 32, 64, 128 };
		Vector<Integer> A = new Vector<>(Arrays.asList(arr));
		ArrayList<Integer> flagList = new ArrayList<>();
		flagList = Combination(A, flag);
		String acceptancerule = "";
		for (int a = 0; a < flagList.size(); a++) {
			if (flagList.get(a) == 1) {
				transfer.setOppDirSmRoute("Y");
				acceptancerule += "ODSR";
			}
			if (flagList.get(a) == 2) {
				transfer.setSameDirSmRoute("Y");
				acceptancerule += "SDSR";
			}
			if (flagList.get(a) == 4) {
				transfer.setOppDirDifRoute("Y");
				acceptancerule += "ODDR";
			}
			if (flagList.get(a) == 8) {
				transfer.setSameDirDifRoute("Y");
				acceptancerule += "SDDR";
			}
			if (flagList.get(a) == 16) {
				transfer.setCapFlag("Y");
			}
			if (flagList.get(a) == 32) {
				transfer.setHoldFlag("Y");
			}
			if (flagList.get(a) == 64) {
				transfer.setUpgredFlag("Y");
			}
			if (flagList.get(a) == 128) {
				transfer.setPayreqFlag("Y");
			}
			if (a < flagList.size() - 1) {
				acceptancerule += ",";
			}
			transfer.setAcceptanceRule(acceptancerule);
		}
	}
	
	
	public void getTripsAndExpResAndExpOff (FaretableTransferDTO transfer , byte binStream, int expoff)
	{
		if (transfer.getGroup() == 0)// transfer
		{
			// compare trips with ticket.getValue
			transfer.setTrips(
					BigInteger.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) / 32)));
			transfer.setExpResolution(BigInteger
					.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) % 32) / 4));
			expoff = (Integer.parseInt(String.format("%02X", binStream), 16) % 4) * 256;
		}
		if (transfer.getGroup() == 1)// period pass
		{
			// compare trips with LegacyTrips
			transfer.setTrips(
					BigInteger.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) / 32)));
			transfer.setExpResolution(BigInteger
					.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) % 32) / 4));
			expoff = (Integer.parseInt(String.format("%02X", binStream), 16) % 4) * 256;
		}
		if (transfer.getGroup() == 2)// Stored Ride
		{
			// compare trips with printFormat.description
			transfer.setTrips(
					BigInteger.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) / 32)));
			expoff = (Integer.parseInt(String.format("%02X", binStream), 16) % 4) * 256;
			transfer.setExpResolution(BigInteger.valueOf(0));
		}
		if (transfer.getGroup() == 3)// Stored Value
		{
			// compare trips with printFormat.description
			transfer.setTrips(
					BigInteger.valueOf((Integer.parseInt(String.format("%02X", binStream), 16) / 32)));
			transfer.setExpResolution(BigInteger.valueOf(0));
		}

	}

	public List<HolidayType> getHolidayList(byte[] binStream) {

		faretable.setHolidays(new FareTableAPIType.Holidays());
		List<HolidayType> holidayTypeList = faretable.getHolidays().getHoliday();
		int control = 0, month = 0, day = 0;
		long holidayStart = 44034, holidayEnd = 44052;
		for (int j = 1; j <= 8; j++) {
			HolidayType holiday = new HolidayType();
			
			int h = 0;
			for (int i = (int) holidayStart; i < (holidayStart + 2); i++) {
				switch(h){
				case 0:
					control = Integer.parseInt(String.format("%02X", binStream[i]), 16) / 16;
					month = Integer.parseInt(String.format("%02X", binStream[i]), 16) % 16;
					break;
				case 1:
					day = Integer.parseInt(String.format("%02X", binStream[i]), 16);
					break;
				case 2:
					h=0;
					break;
				}
				h++;
			}

			if ((month > 0 && day > 0)) {
				DateType date = new DateType();
				GregorianCalendar cl = new GregorianCalendar();
				cl.setTime(new Date());
				XMLGregorianCalendar xmlGregorianCalendar = null;
				try {
					xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cl);
				} catch (DatatypeConfigurationException e) {
				}
				xmlGregorianCalendar.setMonth(month);
				xmlGregorianCalendar.setDay(day);
				date.setValue(xmlGregorianCalendar);
				date.setLocalTime(true);

				holiday.setHolidayDate(date);
			}
			holidayTypeList.add(holiday);
			holidayStart += 2;
			if (holidayStart == holidayEnd) {
				break;
			}
		}
		return holidayTypeList;

	}

	public List<BadListedCardType> getBadListedCards(byte[] binStream) {

		faretable.setBadListedCards(new FareTableAPIType.BadListedCards());
		List<BadListedCardType> badListedCardTypeList = faretable.getBadListedCards().getBadListedCard();

		long badlistStartPosition = 45440,badlistEndPosition = 46240;

		for (int x = 1; x <= 40; x++) {
			BadListedCardType badList = new BadListedCardType();
			String seq1 = "", seq2 = "";
			int agencyid = 0, tpn = 0, r = 0;
			
			for (int i = (int) badlistStartPosition; i < (badlistStartPosition + 20); i++) {
				
				switch(r) {
				case 0: //hardcoded as 0
					break;
				case 1:
					badList.setGroup((byte) Integer.parseInt(String.format("%02X", binStream[i]), 16));
					break;
				case 2:
					badList.setDesignator(Integer.parseInt(String.format("%02X", binStream[i]), 16));
					break;
				case 3:
				case 4:
				case 5:
					seq1 += String.format("%02X", binStream[i]);
					badList.setSequence1(Integer.parseInt(seq1, 16));
					break;
				case 6:
					badList.setSecurityCode(Integer.parseInt(String.format("%02X", binStream[i]), 16) / 16);
					agencyid = (Integer.parseInt(String.format("%02X", binStream[i]), 16) % 16) * 256;
					break;
				case 7:
					agencyid += Integer.parseInt(String.format("%02X", binStream[i]), 16);
					badList.setAgencyID(agencyid);
					break;
				case 8:
					if (badList.getGroup() != 3) {
						badList.setManufactureID(Integer.parseInt(String.format("%02X", binStream[i]), 16) / 16);
						tpn = (Integer.parseInt(String.format("%02X", binStream[i]), 16) % 16) * 256;
					}
					break;
				case 9:
					if (badList.getGroup() != 3) {
						badList.setThirdPartyNumber(tpn + Integer.parseInt(String.format("%02X", binStream[i]), 16));
					}
					break;
				case 13:
				case 14:
				case 15:
					seq2 += String.format("%02X", binStream[i]);
					badList.setSequence2(Integer.parseInt(seq2, 16));
					break;
				case 20:
					r=0;
					break;
				}
				r++;
			}
			badListedCardTypeList.add(badList);
			badlistStartPosition += 20;
			if (badlistStartPosition == badlistEndPosition) {
				break;
			}
		}
		return badListedCardTypeList;
	}
	
	
	public DateType getEffectiveDate(byte[] binStream) {

		String effDateLow = "", effDateHigh = "";
		for (int a = 46241; a > 46239; a--) {
			effDateLow += String.format("%02X", binStream[a]);
		}

		for (int a = 46499; a > 46497; a--) {
			effDateHigh += String.format("%02X", binStream[a]);
		}

		String effDate = effDateHigh + effDateLow;
		int edate = Integer.parseInt(effDate, 16);
		long etime = edate * 1000L;

		Date date = new Date();
		date.setTime(etime);

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);

		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateType dt = new DateType();
		dt.setValue(xmlGregorianCalendar);
		dt.setLocalTime(true);
		return dt;
	}

	public void getCrc16Data(byte[] binStream)
	{
		int startIndex=46500, endIndex=46502;
		String hexCRCdata="";
		
		for(;startIndex<endIndex;startIndex++)
		{
			hexCRCdata += String.format("%02X", binStream[startIndex]);
		}
		//System.out.println("crc in hex :"+hexCRCdata);
		//System.out.println("crc hex to deci :"+Integer.parseInt(hexCRCdata,16));
	}
	
	

	static ArrayList<Integer> Combination(Vector<Integer> A, int K) {
		// Sort the given elements
		Collections.sort(A);
		// To store combination
		Vector<Integer> local = new Vector<Integer>();
		ArrayList<Integer> uniqList = new ArrayList<>();
		uniqList = unique_combination(0, 0, K, local, A, uniqList);
		return uniqList;
	}

	static ArrayList<Integer> unique_combination(int l, int sum, int K, Vector<Integer> local, Vector<Integer> A,
			ArrayList<Integer> uniqList) {
		// If a unique combination is found
		if (sum == K) {
			for (int i = 0; i < local.size(); i++) {
				uniqList.add(local.get(i));
			}
		}

		// For all other combinations
		for (int i = l; i < A.size(); i++) {
			// Check if the sum exceeds K
			if (sum + A.get(i) > K)
				continue;
			// Check if it is repeated or not
			if (i > l && A.get(i) == A.get(i - 1))
				continue;
			// Take the element into the combination
			local.add(A.get(i));
			// Recursive call
			unique_combination(i + 1, sum + A.get(i), K, local, A, uniqList);
			// Remove element from the combination
			local.remove(local.size() - 1);
		}
		return uniqList;
	}

}
