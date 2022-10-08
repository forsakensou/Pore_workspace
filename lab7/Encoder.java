import java.util.Random;

public class Encoder {
    private static String convertHexToString(String hex){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2){ //if-ge 大于等于跳出
            result.append((char)(Integer.parseInt(hex.substring(i, i + 2), 16) ^ 0xFF));
        }
        return result.toString();
    }
    private static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < chars.length; i++) { //v3 = v1.length()
            result.append(Integer.toHexString(chars[i] ^ 0xFF));
        }
        return result.toString();
    }
    private static byte[] getSalt(){
        byte[] result = {0,0,0,0,0,0};
        Random saltnumber = new Random();
        for(int i = 0; i < result.length; i++){
            result[i] = (byte) (saltnumber.nextInt(15));
        }
        return result;
    }
    public static String decode(String str){
        if (str.length() == 0){
            return "";
        }
        StringBuffer result_hex = new StringBuffer();
        for(int i = 0; i < str.length(); i += 5){ //v3 = a3.length()
            int v3 = 4 - (Integer.parseInt(str.substring(i, i+1), 16) % 4); //0x10
            result_hex.append(str.substring(i + 1 + v3, i + 5) + str.substring(i + 1, v3 + i + 1));
        }
        String result = convertHexToString(result_hex.toString());
        return result.substring(0,11);
    }
    public static String encode(String str){
        if(str.length() != 11){ //0xb
            System.out.println("input error!");
            return "";
        }
        byte[] bytes = {12, 12, 8, 7, 6, 3};
        String hex = convertStringToHex(str + "a");
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < hex.length(); i += 4) {
            int tmp = bytes[i / 4] % 4;
            result.append(Integer.toHexString(bytes[i / 4]));
            result.append(hex.substring(i + tmp, i + 4) + hex.substring(i, tmp + i));
        }
        return result.toString();
    }
}
