/**
 * Copyright 2017 Prasanth Jayachandran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.prasanthj.hll.tools;

import com.github.prasanthj.hll.HyperLogLog;
import com.github.prasanthj.hll.HyperLogLogUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class HyperLogLogCLI {

  public static void main(String[] args) {
    long n = 0;
    long seed = 123;
    HyperLogLog.EncodingType enc = HyperLogLog.EncodingType.SPARSE;
    int p = 14;
    boolean bitPack = true;
    boolean noBias = true;
    boolean printRelativeError = false;
    int unique = -1;
    String filePath = null;
    BufferedReader br = null;
    String outFile = null;
    String inFile = null;
    FileOutputStream fos = null;
    DataOutputStream out = null;
    FileInputStream fis = null;
    DataInputStream in = null;
    try {

        System.out.println("Example usage: hll -n 1000\n"
                + "          <OR> hll -f /tmp/input.txt\n"
                + "          <OR> hll -d -i /tmp/out.hll\n"
                + "          <OR> cat file | hll -t\n");

      n=4000;


      // construct hll and serialize it if required
      HyperLogLog hll = HyperLogLog.builder().enableBitPacking(bitPack).enableNoBias(noBias)
          .setEncoding(enc).setNumRegisterIndexBits(p).build();


        Random rand = new Random(seed);
        for (int i = 0; i < n; i++) {
          if (unique < 0) {
            hll.addLong(rand.nextLong());
          } else {
            int val = rand.nextInt(unique);
            hll.addLong(val);
          }
        }

      long estCount = hll.count();
      System.out.println(hll.toString());
      if(printRelativeError) {
        System.out.println("Actual count: " + n);
        System.out.println("Relative error: " + HyperLogLogUtils.getRelativeError(n, estCount) + "%");
      }
      HyperLogLogUtils.serializeHLL(System.out, hll);

      if (fos != null && out != null) {
        long start = System.currentTimeMillis();
        HyperLogLogUtils.serializeHLL(out, hll);
        long end = System.currentTimeMillis();
        System.out.println("Serialized hyperloglog to " + outFile);
        System.out.println("Serialized size: " + out.size() + " bytes");
        System.out.println("Serialization time: " + (end - start) + " ms");
        out.close();
      }
    } catch (NumberFormatException e) {
      System.err.println("Invalid type for parameter.");
    } catch (FileNotFoundException e) {
      System.err.println("Specified file not found.");
    } catch (IOException e) {
      System.err.println("Exception occured while reading file.");
    }
  }


}
