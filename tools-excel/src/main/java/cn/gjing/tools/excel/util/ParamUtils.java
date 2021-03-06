package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.exception.ExcelResolverException;

import java.util.regex.Pattern;

/**
 * Param utils
 *
 * @author Gjing
 **/
public final class ParamUtils {
    private static Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");

    /**
     * Determines whether the array contains a value
     *
     * @param arr array
     * @param val value
     * @return boolean
     */
    public static boolean noContains(String[] arr, String val) {
        if (arr == null || arr.length == 0) {
            return true;
        }
        for (String o : arr) {
            if (o.equals(val)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether it's equal or not
     *
     * @param param1 param1
     * @param param2 param2
     * @param allEmpty Whether allow empty？
     * @return boolean
     */
    public static boolean equals(Object param1, Object param2, boolean allEmpty) {
        if (allEmpty) {
            if (param1 == param2) {
                return true;
            }
            return param1 != null && param1.equals(param2);
        }
        if (param1 == null || "".equals(param1)) {
            return false;
        }
        return param1 == param2 || param1.equals(param2);
    }

    /**
     * Whether obj is null
     * @param obj obj
     * @param message error message
     * @param <T> T
     */
    public static <T> void requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new ExcelResolverException(message);
        }
    }

    /**
     * Whether it's numeric
     *
     * @param str param
     * @return true or false
     */
    public static boolean isNumber(String str) {
        return pattern.matcher(str).matches();
    }

    /**
     * Compute cell formula
     *
     * @param offset   The offset, if you give it to 0, means you start with column A, and 1 means you start with column B
     * @param rowIndex Row index
     * @param colCount How many columns
     * @return If the input parameter is 1,1,10, it means from B1 to K1. Finally return $B$1:$K$1
     */
    public static String createFormula(int offset, int rowIndex, int colCount) {
        char start = (char) ('A' + offset);
        if (colCount <= 25) {
            if (colCount == 0) {
                return "$" + start + "$" + rowIndex;
            } else {
                char end = (char) (start + colCount - 1);
                return "$" + start + "$" + rowIndex + ":$" + end + "$" + rowIndex;
            }

        } else {
            char endPrefix = 'A';
            char endSuffix;
            if ((colCount - 25) / 26 == 0 || colCount == 51) {
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char) ('A' + 25);
                } else {
                    endSuffix = (char) ('A' + (colCount - 25) % 26 - 1);
                }
            } else {
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char) ('A' + 25);
                    endPrefix = (char) (endPrefix + (colCount - 25) / 26 - 1);
                } else {
                    endSuffix = (char) ('A' + (colCount - 25) % 26 - 1);
                    endPrefix = (char) (endPrefix + (colCount - 25) / 26);
                }
            }
            return "$" + start + "$" + rowIndex + ":$" + endPrefix + endSuffix + "$" + rowIndex;
        }
    }

}
