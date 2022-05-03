/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.storage;

// Followed information in https://stackoverflow.com/questions/23122312/crc-calculation-of-a-mostly-static-data-stream/23126768#23126768

class Crc32cUtility {
    private static final int CRC32C_DIM = 32;
    private Crc32cUtility() {}

    static long matrixTimes(long []mat, long vec) {
         long sum = 0;
         int matIndex = 0;
         while(vec != 0 && matIndex < CRC32C_DIM) {
             if((vec & 1) != 0)
                 sum ^= mat[matIndex];
             vec >>= 1;
             matIndex++;
         }
         return sum;
    }

    static void matrixSquare(long []square, long []mat) {
        for(int n = 0; n < CRC32C_DIM; n++) {
            square[n] = matrixTimes(mat, mat[n]);
        }
    }

    static long crc32cCombine(long crc1, long crc2, long len2) {
        long row, even[] = new long[CRC32C_DIM], odd[] = new long[CRC32C_DIM];
        // degenerate case (also disallow negative lengths)
        if(len2 <= 0)
            return crc1;

        // put operator for one zero bit in odd
        odd[0] = 0x82F63B78;
        row = 1;

        for(int n = 1; n < CRC32C_DIM; n++) {
            odd[n] = row;
            row <<= 1;
        }
        // put operator for two zero bits in even
        matrixSquare(even, odd);
        // put operator for four zero bits in odd
        matrixSquare(odd, even);

        // apply len2 zeros to crc1 (first square will put the operator for one zero byte, eight zero bits, in even) */
        do {
            // apply zeros operator for this bit of len2
            matrixSquare(even, odd);
            if ((len2 & 1) != 0)
                crc1 = matrixTimes(even, crc1);
            len2 >>= 1;

            // if no more bits set, then done
            if (len2 == 0)
                break;

            // another iteration of the loop with odd and even swapped
            matrixSquare(odd, even);
            if ((len2 & 1) != 0)
                crc1 = matrixTimes(odd, crc1);
            len2 >>= 1;
            // if no more bits set, then done
        } while(len2 != 0);
        crc1 ^= crc2;
        return crc1;
    }
}

