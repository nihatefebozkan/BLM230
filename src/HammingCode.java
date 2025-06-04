public class HammingCode {
    // 2'nin kuvveti mi kontrolü
    public static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0 && x != 0;
    }

    // Toplam kod uzunluğunu hesapla (veri + kontrol bitleri + genel parite)
    public static int getTotalLength(int dataLen) {
        int r = 1;
        while (Math.pow(2, r) < dataLen + r + 1) r++;
        return dataLen + r + 1; // +1 genel parite için
    }

    // Hamming kodu oluştur
    public static String calculateHammingCode(String data) {
        int dataLen = data.length();
        int totalBits = getTotalLength(dataLen);
        int[] code = new int[totalBits + 1]; // 1-based index
        int dataIndex = 0;

        // Veri bitlerini yerleştir
        for (int i = 1; i <= totalBits; i++) {
            if (!isPowerOfTwo(i) && i != totalBits) {
                code[i] = data.charAt(dataIndex++) - '0';
            }
        }

        // Kontrol bitlerini hesapla
        for (int i = 1; i < totalBits; i <<= 1) {
            int parity = 0;
            for (int j = 1; j < totalBits; j++) {
                if ((j & i) != 0) parity ^= code[j];
            }
            code[i] = parity;
        }

        // Genel parite biti
        int overallParity = 0;
        for (int i = 1; i < totalBits; i++) overallParity ^= code[i];
        code[totalBits] = overallParity;

        // Sonucu string olarak döndür
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= totalBits; i++) result.append(code[i]);
        return result.toString();
    }

    // Rastgele hata oluştur
    public static String introduceRandomError(String code) {
        int pos = (int) (Math.random() * code.length());
        char[] codeArray = code.toCharArray();
        codeArray[pos] = codeArray[pos] == '0' ? '1' : '0';
        return new String(codeArray) + "," + (pos + 1);
    }

    // Belirli pozisyonda hata oluştur
    public static String introduceErrorAtPosition(String code, int pos) {
        if (pos < 1 || pos > code.length()) return code + ",Hatalı pozisyon";
        char[] codeArray = code.toCharArray();
        codeArray[pos - 1] = codeArray[pos - 1] == '0' ? '1' : '0';
        return new String(codeArray) + "," + pos;
    }

    // Hata kontrolü ve düzeltme
    public static String[] detectAndCorrectError(String codeStr, String originalData) {
        int totalBits = codeStr.length();
        int[] code = new int[totalBits + 1];
        for (int i = 0; i < totalBits; i++) code[i + 1] = codeStr.charAt(i) - '0';

        int errorPos = 0;
        int r = 0;
        while (Math.pow(2, r) < totalBits) r++;
        int[] syndrome = new int[r];

        // Kontrol bitlerini kontrol et
        for (int i = 0; i < r; i++) {
            int checkBit = 1 << i;
            int parity = 0;
            for (int j = 1; j < totalBits; j++) {
                if ((j & checkBit) != 0) parity ^= code[j];
            }
            syndrome[i] = parity;
            if (parity != 0) errorPos += checkBit;
        }

        // Genel parite kontrolü
        int overallParity = 0;
        for (int i = 1; i <= totalBits; i++) overallParity ^= code[i];

        StringBuilder hataDurumu = new StringBuilder();
        StringBuilder duzeltilmisKod = new StringBuilder(codeStr);

        if (overallParity == 0 && errorPos == 0) {
            hataDurumu.append("Hata yok.");
        } else if (overallParity == 1 && errorPos > 0) {
            code[errorPos] ^= 1;
            hataDurumu.append("Hata ").append(errorPos).append(". bitte bulundu ve düzeltildi.");
            duzeltilmisKod = new StringBuilder();
            for (int i = 1; i <= totalBits; i++) duzeltilmisKod.append(code[i]);
        } else {
            hataDurumu.append("Çift hata algılandı, düzeltilemedi!");
            duzeltilmisKod = new StringBuilder();
        }

        return new String[]{hataDurumu.toString(), duzeltilmisKod.toString(), String.valueOf(overallParity)};
    }

    // Veri doğrulama
    public static boolean isValidBinary(String data, int bitLength) {
        if (data.length() != bitLength) return false;
        for (char c : data.toCharArray()) {
            if (c != '0' && c != '1') return false;
        }
        return true;
    }
}