/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer;

import com.google.android.exoplayer.util.Util;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;

/**
 * Contains information about a media decoder.
 */
@TargetApi(16)
public final class DecoderInfo {

  /**
   * The name of the decoder.
   * <p>
   * May be passed to {@link android.media.MediaCodec#createByCodecName(String)} to create an
   * instance of the decoder.
   */
  public final String name;

  /**
   * Whether the decoder supports seamless resolution switches.
   *
   * @see android.media.MediaCodecInfo.CodecCapabilities#isFeatureSupported(String)
   * @see android.media.MediaCodecInfo.CodecCapabilities#FEATURE_AdaptivePlayback
   */
  public final boolean adaptive;

  private final CodecCapabilities capabilities;

  /**
   * @param name The name of the decoder.
   */
  /* package */ DecoderInfo(String name) {
    this.name = name;
    this.adaptive = false;
    this.capabilities = null;
  }

  /**
   * @param name The name of the decoder.
   * @param capabilities The capabilities of the decoder.
   */
  /* package */ DecoderInfo(String name, CodecCapabilities capabilities) {
    this.name = name;
    this.capabilities = capabilities;
    adaptive = isAdaptive(capabilities);
  }

  /**
   * The profile levels supported by the decoder.
   *
   * @return The profile levels supported by the decoder.
   */
  public CodecProfileLevel[] getProfileLevels() {
    return capabilities == null || capabilities.profileLevels == null ? new CodecProfileLevel[0]
        : capabilities.profileLevels;
  }

  /**
   * Whether the decoder supports video with a specified width and height.
   * <p>
   * Must not be called if the device SDK version is less than 21.
   *
   * @param width Width in pixels.
   * @param height Height in pixels.
   * @return Whether the decoder supports video with the given width and height.
   */
  @TargetApi(21)
  public boolean isVideoSizeSupportedV21(int width, int height) {
    if (capabilities == null) {
      return false;
    }
    MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
    return videoCapabilities != null && videoCapabilities.isSizeSupported(width, height);
  }

  /**
   * Whether the decoder supports video with a given width, height and frame rate.
   * <p>
   * Must not be called if the device SDK version is less than 21.
   *
   * @param width Width in pixels.
   * @param height Height in pixels.
   * @param frameRate Frame rate in frames per second.
   * @return Whether the decoder supports video with the given width, height and frame rate.
   */
  @TargetApi(21)
  public boolean isVideoSizeAndRateSupportedV21(int width, int height, double frameRate) {
    if (capabilities == null) {
      return false;
    }
    MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
    return videoCapabilities != null && videoCapabilities.areSizeAndRateSupported(width, height,
        frameRate);
  }

  /**
   * Whether the decoder supports audio with a given sample rate.
   * <p>
   * Must not be called if the device SDK version is less than 21.
   *
   * @param sampleRate The sample rate in Hz.
   * @return Whether the decoder supports audio with the given sample rate.
   */
  @TargetApi(21)
  public boolean isAudioSampleRateSupportedV21(int sampleRate) {
    if (capabilities == null) {
      return false;
    }
    MediaCodecInfo.AudioCapabilities audioCapabilities = capabilities.getAudioCapabilities();
    return audioCapabilities != null && audioCapabilities.isSampleRateSupported(sampleRate);
  }

  /**
   * Whether the decoder supports audio with a given channel count.
   * <p>
   * Must not be called if the device SDK version is less than 21.
   *
   * @param channelCount The channel count.
   * @return Whether the decoder supports audio with the given channel count.
   */
  @TargetApi(21)
  public boolean isAudioChannelCountSupportedV21(int channelCount) {
    if (capabilities == null) {
      return false;
    }
    MediaCodecInfo.AudioCapabilities audioCapabilities = capabilities.getAudioCapabilities();
    return audioCapabilities != null && audioCapabilities.getMaxInputChannelCount() >= channelCount;
  }

  private static boolean isAdaptive(CodecCapabilities capabilities) {
    if (Util.SDK_INT >= 19) {
      return isAdaptiveV19(capabilities);
    } else {
      return false;
    }
  }

  @TargetApi(19)
  private static boolean isAdaptiveV19(CodecCapabilities capabilities) {
    return capabilities.isFeatureSupported(CodecCapabilities.FEATURE_AdaptivePlayback);
  }

}
