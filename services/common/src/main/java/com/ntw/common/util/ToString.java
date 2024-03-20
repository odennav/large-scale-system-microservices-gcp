//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.common.util;

import java.util.Collection;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * ToString class provides toString representation for Collection classes
 */
public class ToString {
    public static <T> String toString(Collection<T> items) {
        StringBuilder builder = new StringBuilder("[");
        if (items != null) {
            int count = 0;
            for (T item : items) {
                builder.append(item.toString());
                if (++count < items.size()) {
                    builder.append(", ");
                }
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
