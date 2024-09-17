package com.wzm.aio.util;

import ws.schild.jave.*;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.AudioInfo;

import java.io.File;

public abstract class AudioConverter {

    private static final String WAV = "wav";
    private static final String WAV_CODEC = "pcm_s16le";

    /**
     * 将指定音频文件转换为wav格式，保持采样率和通道不变
     * 新音频文件将保存到源音频文件所在的文件夹
     *
     * @param source 源音频文件
     */
    public static File convertToWAV(File source) {
        String parentDir = source.getParentFile().getPath();
        return convertToWAV(source, parentDir);
    }

    public static File convertToWAV(File source, Encoder encoder) {
        String parentDir = source.getParentFile().getPath();
        return convertToWAV(source, parentDir, encoder);
    }

    public static File convertToWAV(File source, String outputDir) {
        return convertToWAV(source, outputDir, new Encoder());
    }

    /**
     * 将指定音频文件转换为wav格式，保持采样率和通道不变
     * 新音频文件将保存到outputDir指定的文件夹
     *
     * @param source    源音频文件
     * @param outputDir 转换后文件所在的文件夹
     */

    public static File convertToWAV(File source, String outputDir, Encoder encoder) {
        String sourceName = source.getName().split("\\.")[0];
        File target = new File(outputDir, sourceName + ".wav");
        return convertToWAV(source,target,encoder);
    }

    public static File convertToWAV(File source, File target, Encoder encoder) {
        MultimediaObject sourceMultimediaObject = new MultimediaObject(source);
        try {
            AudioInfo audioInfo = sourceMultimediaObject.getInfo().getAudio();
            // 设置转换属性
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec(WAV_CODEC); // 设置编码方式为 WAV 格式的 PCM
            audio.setSamplingRate(audioInfo.getSamplingRate()); // 设置采样率
            audio.setChannels(audioInfo.getChannels());        // 设置通道（立体声）
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat(WAV);
            attrs.setAudioAttributes(audio);
            attrs.setMapMetaData(true);
            encoder.encode(sourceMultimediaObject, target, attrs);
            return target;
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }
}
