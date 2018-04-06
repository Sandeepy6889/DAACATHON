package com.siemens.primecult.alarmservices;

import java.util.HashMap;
import java.util.Map;

public class FrequencySamplingService {

	public static Map<Float, Float> createFrequencySamples(float[]amplitudes,
			float samplingFrequency){
		
		Map<Float,Float> freqAmpMap = new HashMap<>();
		float freq = 0;
		float sampleRate = samplingFrequency/amplitudes.length;
		for(float amp : amplitudes)
		{
			freqAmpMap.put(freq/60, amp);
			freq = freq +sampleRate;
		}		
		return freqAmpMap;		
	}
}
