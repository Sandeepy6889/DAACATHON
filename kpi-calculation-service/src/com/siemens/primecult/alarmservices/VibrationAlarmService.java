package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.ValueRt;

public class VibrationAlarmService {

	private static float[] defectFrequencies = { 150, 300, 100, 200, 250, 350, 50 };
	
	private static final BigDecimal THIRTY_PERCENT = new BigDecimal(0.3).setScale(4, BigDecimal.ROUND_DOWN);

	
	public static boolean checkVibrationAlarmStateChange(AlarmTypes alarmType, ValueRt rawValues,
			Map<String, List<Integer>> currentAlarmsStatus) {

		int currentState = checkforImpellerWearingFault(FrequencySamplingService
				.createFrequencySamples(rawValues)) ? RAISED: GONE;
		int previousStae = currentAlarmsStatus.get(rawValues.getAssetID()).get(alarmType.getIndex());
		return (currentState != previousStae);
	}

	private static boolean checkforImpellerWearingFault(Map<Float, BigDecimal> freqAmpMapping) {

		BigDecimal[] topThreeAmpl = findThreeHighestAmp(freqAmpMapping);
		double averageAmp = findAvgOfAmplitudes(freqAmpMapping.values());
		float[] defectFrequencies = findActualValuesOfDefectFrequencies(freqAmpMapping, 4);
		BigDecimal onetwentyPercAvgAmp = new BigDecimal(averageAmp + averageAmp * (0.2)).setScale(4, BigDecimal.ROUND_DOWN);

		// check for 1st combination
		if (freqAmpMapping.get(defectFrequencies[0]).equals(topThreeAmpl[0]))
			if (freqAmpMapping.get(defectFrequencies[2]).compareTo(topThreeAmpl[0].multiply(THIRTY_PERCENT))>=0
					&& freqAmpMapping.get(defectFrequencies[3]).compareTo(topThreeAmpl[0].multiply(THIRTY_PERCENT))>=0)
				if (freqAmpMapping.get(defectFrequencies[1]).compareTo(onetwentyPercAvgAmp)>=0
						&& freqAmpMapping.get(defectFrequencies[6]).compareTo(onetwentyPercAvgAmp)>=0)
					return true;
		// check for 2nd combination
		if (freqAmpMapping.get(defectFrequencies[0]).equals(topThreeAmpl[0]))
			if (freqAmpMapping.get(defectFrequencies[4]).compareTo(topThreeAmpl[0].multiply(THIRTY_PERCENT))>=0
					&& freqAmpMapping.get(defectFrequencies[5]).compareTo(topThreeAmpl[0].multiply(THIRTY_PERCENT))>=0)
				if (freqAmpMapping.get(defectFrequencies[0]).compareTo(onetwentyPercAvgAmp)>=0
						&& freqAmpMapping.get(defectFrequencies[6]).compareTo(onetwentyPercAvgAmp)>=0)
					return true;
		// check for 3rd combination
		BigDecimal[] ampOfOneSevAndTwo = { freqAmpMapping.get(defectFrequencies[0]),
				freqAmpMapping.get(defectFrequencies[1]), freqAmpMapping.get(defectFrequencies[6]) };
		Arrays.sort(ampOfOneSevAndTwo);
		if (Arrays.equals(topThreeAmpl, ampOfOneSevAndTwo))
			if (freqAmpMapping.get(defectFrequencies[2]).compareTo(freqAmpMapping.get(defectFrequencies[0]).multiply(THIRTY_PERCENT))>=0
					|| freqAmpMapping.get(defectFrequencies[3]).compareTo(freqAmpMapping.get(defectFrequencies[0]).multiply(THIRTY_PERCENT))>=0)
				if (freqAmpMapping.get(defectFrequencies[4]).compareTo(freqAmpMapping.get(defectFrequencies[1]).multiply(THIRTY_PERCENT))>=0
						|| freqAmpMapping.get(defectFrequencies[5]).compareTo(freqAmpMapping.get(defectFrequencies[1]).multiply(THIRTY_PERCENT))>=0)
					return true;
		/// to do///
		return false;
	}

	private static float[] findActualValuesOfDefectFrequencies(Map<Float, BigDecimal> freqAmpMapping, float range) {

		float[] actualDefectFreq = new float[7];
		for (float freq : freqAmpMapping.keySet()) {
			if (freq >= defectFrequencies[0] - range && freq <= defectFrequencies[0] + range)
				actualDefectFreq[0] = freq;
			else if (freq >= defectFrequencies[1] - range && freq <= defectFrequencies[1] + range)
				actualDefectFreq[1] = freq;
			else if (freq >= defectFrequencies[2] - range && freq <= defectFrequencies[2] + range)
				actualDefectFreq[2] = freq;
			else if (freq >= defectFrequencies[3] - range && freq <= defectFrequencies[3] + range)
				actualDefectFreq[3] = freq;
			else if (freq >= defectFrequencies[4] - range && freq <= defectFrequencies[4] + range)
				actualDefectFreq[4] = freq;
			else if (freq >= defectFrequencies[5] - range && freq <= defectFrequencies[5] + range)
				actualDefectFreq[5] = freq;
			else if (freq >= defectFrequencies[6] - range && freq <= defectFrequencies[6] + range)
				actualDefectFreq[6] = freq;
		}
		return actualDefectFreq;
	}

	private static BigDecimal[] findThreeHighestAmp(Map<Float, BigDecimal> freqAmpMapping) {
		BigDecimal topThreeAmp[] = { new BigDecimal(0),new BigDecimal(0),new BigDecimal(0) };
		for (float freq : freqAmpMapping.keySet()) {
			if (freqAmpMapping.get(freq).compareTo(topThreeAmp[0]) == 1) {
				topThreeAmp[2] = topThreeAmp[1];
				topThreeAmp[1] = topThreeAmp[0];
				topThreeAmp[0] = freqAmpMapping.get(freq);
			} else if (freqAmpMapping.get(freq).compareTo(topThreeAmp[1]) == 1) {
				topThreeAmp[2] = topThreeAmp[1];
				topThreeAmp[1] = freqAmpMapping.get(freq);
			} else if (freqAmpMapping.get(freq).compareTo(topThreeAmp[2]) == 1) {
				topThreeAmp[2] = freqAmpMapping.get(freq);
			}
		}
		return topThreeAmp;
	}

	private static double findAvgOfAmplitudes(Collection<BigDecimal> amplitudes) {
		BigDecimal sum = new BigDecimal(0).setScale(4, BigDecimal.ROUND_DOWN);
		for (BigDecimal amp : amplitudes)
			sum = sum.add(amp);
		return (sum.divide(new BigDecimal(amplitudes.size()).setScale(4, BigDecimal.ROUND_DOWN))).doubleValue();
	}

	/*public static void main(String[] args) {

		float[] ampArray = {0.639588925f,-0.843515652f,-1.814916899f,0.256660608f,
				1.441098879f,0.2645025f,-0.484934599f,0.975416844f,1.021705937f,
				-0.981929017f,-0.689522735f,0.571403098f,0.821074445f,0.277298455f,
				-1.123855599f,0.665026026f,0.585799315f,1.080349878f,-0.330868107f,
				-0.472598488f,0.410218205f,0.823747678f,-0.613660562f,
				2.21675342f,0.702618356f,0.275937314f,-1.231741384f,0.28815693f,
				1.364298621f,-0.501268296f,0.675026026f,0.2208724f,0.582690762f,
				0.215090614f,-0.161835191f,-0.639172766f,0.477760249f,-0.049295775f,
				-0.268586691f,-0.653955252f,-0.520919008f,0.258873996f,1.324758077f,
				-0.687180591f,-1.406415066f,1.740733926f,1.020805937f,2.215675776f,
				0.341842309f,0.567876734f,-1.042821156f,-0.741840842f,0.227671975f,
				0.317746427f,-1.139441281f,0.153514294f,1.929699592f,-0.252283651f,
				-1.233642076f,0.910964959f,0.030239028f,-1.625141905f,-0.301554517f,
				1.964286073f};
		System.out.println(
				checkforImpellerWearingFault(FrequencySamplingService.createFrequencySamples(ampArray, 25560)));
	}*/
}
