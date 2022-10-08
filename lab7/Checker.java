public class Checker {
    static byte[] array1 = {0x70,0x64,0x64,0x44,0x1f,0x5,0x72,0x78};
    private static byte charToByteAscii(char chr){
        return (byte)chr;
    }
    private static boolean CheckStr1(String str){
        for(int i = 0; i < str.length(); i++) {       //v2 = a2.length()
            if((charToByteAscii(str.charAt(i)) ^ i * 11) != array1[i]) { //0xb
                return false;       //boolean v1 = false or true
            }
        }
        return true;
    }
    private static boolean CheckStr2(String str){
        try {
            int str2int = Integer.parseInt(str);
            if(str2int >= 1000) { //0x3e8
                if(str2int % 16 == 0 || str2int % 27 == 0 || str2int % 10 == 8) { //0x10, 0x1b
                    return true;
                }
            }
        }
        catch(NumberFormatException v1) { //move-exception v1
        }
        return false;
    }
    public static boolean check(String str){
        if (str.length() != 12){ //0xc
            return false;
        }
        return (CheckStr1(str.substring(0,8)) && CheckStr2(str.substring(8,12)));
    }
}
