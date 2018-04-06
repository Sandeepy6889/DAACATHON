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
		float [] defectFrequencies = findActualValuesOfDefectFrequencies(freqAmpMapping, 3);
		// check for 1st combination
		if (freqAmpMapping.get(defectFrequencies[0]) == topThreeAmpl[0])
			if (freqAmpMapping.get(defectFrequencies[2]) >= topThreeAmpl[0] * (0.3)
					&& freqAmpMapping.get(defectFrequencies[3]) >= topThreeAmpl[0] * (0.3))
				if (freqAmpMapping.get(defectFrequencies[1]) * (0.2) <= averageAmp
						&& freqAmpMapping.get(defectFrequencies[6]) * (0.2) >= averageAmp)
					return true;
		// check for 2nd combination
		if (freqAmpMapping.get(defectFrequencies[1]) == topThreeAmpl[0])
			if (freqAmpMapping.get(defectFrequencies[4]) >= topThreeAmpl[0] * (0.3)
					&& freqAmpMapping.get(defectFrequencies[5]) >= topThreeAmpl[0] * (0.3))
				if (freqAmpMapping.get(defectFrequencies[0]) >= averageAmp * (0.2)
						&& freqAmpMapping.get(defectFrequencies[6]) >= averageAmp * (0.2))
					return true;
		// check for 3rd combination
		float[] ampOfOneSevAndTwo = { freqAmpMapping.get(defectFrequencies[0]),
				freqAmpMapping.get(defectFrequencies[1]), freqAmpMapping.get(defectFrequencies[6]) };
		Arrays.sort(ampOfOneSevAndTwo);
		if (Arrays.equals(topThreeAmpl, ampOfOneSevAndTwo))
			if (freqAmpMapping.get(defectFrequencies[2]) <= freqAmpMapping.get(defectFrequencies[0]) * (0.3)
					|| freqAmpMapping.get(defectFrequencies[3]) <= freqAmpMapping.get(defectFrequencies[0]) * (0.3))
				if (freqAmpMapping.get(defectFrequencies[4]) <= freqAmpMapping.get(defectFrequencies[1]) * (0.3)
						|| freqAmpMapping.get(defectFrequencies[5]) <= freqAmpMapping.get(defectFrequencies[1]) * (0.3))
					return true;
		/// to do///
		return false;
	}

	private static float [] findActualValuesOfDefectFrequencies(Map<Float, Float> freqAmpMapping, float range) {

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
			if (topThreeAmp[0] < Math.abs(freqAmpMapping.get(freq))) {
				topThreeAmp[2] = topThreeAmp[1];
				topThreeAmp[1] = topThreeAmp[0];
				topThreeAmp[0] = Math.abs(freqAmpMapping.get(freq));
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
}
