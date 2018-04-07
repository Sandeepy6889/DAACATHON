package com.siemens.primecult.alarmservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.models.ValueRt;

public class FrequencySamplingService {
	
	private static Map<String,List<List<Float>>> assetVibrationData = new HashMap<>();

	public static Map<Float, BigDecimal> createFrequencySamples(ValueRt rawValues){
		
		Map<Float,BigDecimal> freqAmpMap = new HashMap<>();
		float [] amplitudes = rawValues.getVibrationAmplitudes();
		float freq = 0;
		float sampleRate = rawValues.getSamplingFrequency()/amplitudes.length;
		List<List<Float>> freqAmpListForFFT = new ArrayList<>();
		for(float amp : amplitudes)
		{
			List<Float> freqAmp = new ArrayList<>();
			freqAmp.add(freq/60);freqAmp.add(amp);
			freqAmpListForFFT.add(freqAmp);
			
			freqAmpMap.put(freq/60, new BigDecimal(Math.abs(amp)).setScale(4,BigDecimal.ROUND_DOWN));
			freq = freq +sampleRate;
		}
		
		assetVibrationData.put(rawValues.getAssetID(), freqAmpListForFFT);
		return freqAmpMap;		
	}
	
	public static List<List<Float>> getFFTData(String assetId){
		
		return assetVibrationData.get(assetId);
	}
}