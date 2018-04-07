package com.siemens.primecult.alarmservices;

import static com.siemens.primecult.constants.AlarmStatus.GONE;
import static com.siemens.primecult.constants.AlarmStatus.RAISED;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.constants.AlarmTypes;
import com.siemens.primecult.models.ValueRt;

public class VibrationAlarmService {

	private static float[] defectFrequencies = { 150, 300, 100, 200, 250, 350, 50 };

	public static boolean checkVibrationAlarmStateChange(AlarmTypes alarmType, ValueRt rawValues,
			Map<String, List<Integer>> currentAlarmsStatus) {

		int currentState = checkforImpellerWearingFault(FrequencySamplingService
				.createFrequencySamples(rawValues.getVibrationAmplitudes(), rawValues.getSamplingFrequency())) ? RAISED
						: GONE;
		int previousStae = currentAlarmsStatus.get(rawValues.getAssetID()).get(alarmType.getIndex());
		return (currentState != previousStae);
	}

	private static boolean checkforImpellerWearingFault(Map<Float, Float> freqAmpMapping) {

		float[] topThreeAmpl = findThreeHighestAmp(freqAmpMapping);
		float averageAmp = findAvgOfAmplitudes(freqAmpMapping.values());
		float[] defectFrequencies = findActualValuesOfDefectFrequencies(freqAmpMapping, 4);
		// check for 1st combination
		if (freqAmpMapping.get(defectFrequencies[0]) == topThreeAmpl[0])
			if (freqAmpMapping.get(defectFrequencies[2]) >= topThreeAmpl[0] * (0.3)
					&& freqAmpMapping.get(defectFrequencies[3]) >= topThreeAmpl[0] * (0.3))
				if (freqAmpMapping.get(defectFrequencies[1]) >= (averageAmp + averageAmp * (0.2))
						&& freqAmpMapping.get(defectFrequencies[6]) >= (averageAmp + averageAmp * (0.2)))
					return true;
		// check for 2nd combination
		if (freqAmpMapping.get(defectFrequencies[1]) == topThreeAmpl[0])
			if (freqAmpMapping.get(defectFrequencies[4]) >= topThreeAmpl[0] * (0.3)
					&& freqAmpMapping.get(defectFrequencies[5]) >= topThreeAmpl[0] * (0.3))
				if (freqAmpMapping.get(defectFrequencies[0]) >= (averageAmp + averageAmp * (0.2))
						&& freqAmpMapping.get(defectFrequencies[6]) >= (averageAmp + averageAmp * (0.2)))
					return true;
		// check for 3rd combination
		float[] ampOfOneSevAndTwo = { freqAmpMapping.get(defectFrequencies[0]),
				freqAmpMapping.get(defectFrequencies[1]), freqAmpMapping.get(defectFrequencies[6]) };
		Arrays.sort(ampOfOneSevAndTwo);
		if (Arrays.equals(topThreeAmpl, ampOfOneSevAndTwo))
			if (freqAmpMapping.get(defectFrequencies[2]) >= freqAmpMapping.get(defectFrequencies[0]) * (0.3)
					|| freqAmpMapping.get(defectFrequencies[3]) >= freqAmpMapping.get(defectFrequencies[0]) * (0.3))
				if (freqAmpMapping.get(defectFrequencies[4]) >= freqAmpMapping.get(defectFrequencies[1]) * (0.3)
						|| freqAmpMapping.get(defectFrequencies[5]) >= freqAmpMapping.get(defectFrequencies[1]) * (0.3))
					return true;
		/// to do///
		return false;
	}

	private static float[] findActualValuesOfDefectFrequencies(Map<Float, Float> freqAmpMapping, float range) {

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

	private static float[] findThreeHighestAmp(Map<Float, Float> freqAmpMapping) {
		float topThreeAmp[] = { 0, 0, 0 };
		for (float freq : freqAmpMapping.keySet()) {
			if (topThreeAmp[0] < freqAmpMapping.get(freq)) {
				topThreeAmp[2] = topThreeAmp[1];
				topThreeAmp[1] = topThreeAmp[0];
				topThreeAmp[0] = freqAmpMapping.get(freq);
			} else if (topThreeAmp[1] < freqAmpMapping.get(freq)) {
				topThreeAmp[2] = topThreeAmp[1];
				topThreeAmp[1] = freqAmpMapping.get(freq);
			} else if (topThreeAmp[2] < freqAmpMapping.get(freq)) {
				topThreeAmp[2] = freqAmpMapping.get(freq);
			}
		}
		return topThreeAmp;
	}

	private static float findAvgOfAmplitudes(Collection<Float> amplitudes) {
		float sum = 0;
		for (float amp : amplitudes)
			sum = sum + amp;
		return (sum / amplitudes.size());
	}

	public static void main(String[] args) {

		float[] ampArray = { -0.743515652f, -1.714916899f, 0.356660608f, 1.541098879f, 0.3645025f, -0.384934599f,
				1.075416844f, 0.288346025f, 0.049096248f, -0.589522735f, 0.671403098f, 0.921074445f, 0.377298455f,
				-1.023855599f, 0.319946578f, 0.840785507f, 0.180349878f, -0.230868107f, -0.372598488f, 0.510218205f,
				0.923747678f, -0.513660562f, -0.94487261f, 2.802618356f, 0.375937314f, -1.131741384f, 0.38815693f,
				1.464298621f, -0.401268296f, -0.919697626f, 0.850785507f, 0.682690762f, 0.315090614f, -0.061835191f,
				-0.539172766f, 0.577760249f, 0.050704225f, -0.168586691f, -0.553955252f, -0.420919008f, 0.358873996f,
				1.424758077f, -0.587180591f, -1.306415066f, 1.840733926f, 0.049496248f, 2.315675776f, 0.441842309f,
				0.667876734f, 0.057178844f, -0.641840842f, 0.327671975f, 0.417746427f, -1.039441281f, 0.253514294f,
				2.029699592f, -0.152283651f, -1.133642076f, 1.010964959f, 0.130239028f, -1.525141905f, -0.201554517f,
				2.064286073f };
		System.out.println(
				checkforImpellerWearingFault(FrequencySamplingService.createFrequencySamples(ampArray, 25560)));
	}
}
