package com.siemens.primecult.alarmservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.primecult.models.ValueRt;

public class FrequencySamplingService {
	
	private static Map<String,List<List<Float>>> assetVibrationData = new HashMap<>();

	public static void createFrequencySamples(ValueRt rawValues){
		
		float [] amplitudes = rawValues.getValues();
		float freq = 0;
		float sampleRate = rawValues.getSamplingFrequency()/(amplitudes.length-1);
		List<List<Float>> freqAmpListForFFT = new ArrayList<>();
		for(float amp : amplitudes)
		{
			List<Float> freqAmp = new ArrayList<>();
			freqAmp.add(freq/60);
			freqAmp.add(amp);
			freqAmpListForFFT.add(freqAmp);			
			freq = freq +sampleRate;
		}		
		assetVibrationData.put(rawValues.getAssetID(), freqAmpListForFFT);
		return;		
	}
	
	public static List<List<Float>> getFrequencyAmplitudeData(String assetId){
		return assetVibrationData.get(assetId);
	}
	
	public static String removeFrequencyAmplitudeData(String assetId){
		if(assetVibrationData.get(assetId) == null)
			return "asset not configured";
		assetVibrationData.remove(assetId);
		return "success";
	}
}