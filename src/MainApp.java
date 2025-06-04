import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.text.*;

public class MainApp {
    private JFrame frame;
    private JTextField dataInput;
    private JComboBox<String> bitLengthCombo;
    private JComboBox<Integer> errorPosCombo;
    private JLabel hammingCodeLabel;
    private JLabel overallParityLabel;
    private JLabel correctedCodeLabel;
    private JLabel errorStatusLabel;
    private JLabel bitPositionsLabel;
    private JPanel descriptionPanel;
    private boolean descriptionVisible = false;

    public MainApp() {
        // Ana çerçeve
        frame = new JFrame("Hamming SEC-DED Simülatörü");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(15, 15));

        // Gradient arka plan için özel panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(179, 229, 252), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        frame.add(mainPanel);

        // Başlık paneli
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Hamming SEC-DED Simülatörü", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 36));
        titleLabel.setForeground(new Color(2, 136, 209)); // #0288D1
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Merkez panel (giriş ve çıktılar)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Giriş paneli
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.insets = new Insets(5, 5, 5, 5);
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        // Bit uzunluğu seçimi
        JLabel bitLengthLabel = new JLabel("Bit Uzunluğu Seç:");
        styleLabel(bitLengthLabel);
        bitLengthCombo = new JComboBox<>(new String[]{"8 Bit", "16 Bit", "32 Bit"});
        bitLengthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bitLengthCombo.setBackground(Color.WHITE);
        bitLengthCombo.setBorder(BorderFactory.createLineBorder(new Color(79, 195, 247), 1)); // #4FC3F7

        // Bit uzunluğu değiştiğinde hata pozisyonlarını güncelle
        bitLengthCombo.addActionListener(e -> updateErrorPositions());

        // Veri girişi
        JLabel dataLabel = new JLabel("Veri Girişi:");
        styleLabel(dataLabel);
        dataInput = new JTextField(20);
        dataInput.setFont(new Font("Consolas", Font.PLAIN, 16));
        dataInput.setBackground(Color.WHITE);
        dataInput.setBorder(BorderFactory.createLineBorder(new Color(79, 195, 247), 1));

        // JTextField'a karakter sınırı eklemek için DocumentFilter
        AbstractDocument document = (AbstractDocument) dataInput.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int bitLength = Integer.parseInt(bitLengthCombo.getSelectedItem().toString().split(" ")[0]);
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.length() <= bitLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                int bitLength = Integer.parseInt(bitLengthCombo.getSelectedItem().toString().split(" ")[0]);
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
                if (newText.length() <= bitLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        });

        // Hata pozisyonu
        JLabel errorPosLabel = new JLabel("Hata Pozisyonu Seç:");
        styleLabel(errorPosLabel);
        errorPosCombo = new JComboBox<>();
        errorPosCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorPosCombo.setBackground(Color.WHITE);
        errorPosCombo.setBorder(BorderFactory.createLineBorder(new Color(79, 195, 247), 1));

        // Butonlar
        JButton codeButton = new JButton("Kodla");
        styleButton(codeButton);
        JButton randomErrorButton = new JButton("Rastgele Hata Oluştur");
        styleButton(randomErrorButton);
        JButton errorAtPosButton = new JButton("Seçili Bit'te Hata Oluştur");
        styleButton(errorAtPosButton);
        JButton checkErrorButton = new JButton("Hata Tespit & Düzelt");
        styleButton(checkErrorButton);

        // Giriş paneline ekle
        inputGbc.gridx = 0;
        inputGbc.gridy = 0;
        inputPanel.add(bitLengthLabel, inputGbc);
        inputGbc.gridx = 1;
        inputPanel.add(bitLengthCombo, inputGbc);
        inputGbc.gridx = 0;
        inputGbc.gridy = 1;
        inputPanel.add(dataLabel, inputGbc);
        inputGbc.gridx = 1;
        inputPanel.add(dataInput, inputGbc);
        inputGbc.gridx = 0;
        inputGbc.gridy = 2;
        inputPanel.add(errorPosLabel, inputGbc);
        inputGbc.gridx = 1;
        inputPanel.add(errorPosCombo, inputGbc);
        inputGbc.gridx = 0;
        inputGbc.gridy = 3;
        inputPanel.add(codeButton, inputGbc);
        inputGbc.gridx = 1;
        inputPanel.add(randomErrorButton, inputGbc);
        inputGbc.gridx = 0;
        inputGbc.gridy = 4;
        inputPanel.add(errorAtPosButton, inputGbc);
        inputGbc.gridx = 1;
        inputPanel.add(checkErrorButton, inputGbc);

        // Çıktı paneli
        JPanel outputPanel = new JPanel(new GridBagLayout());
        outputPanel.setOpaque(false);
        GridBagConstraints outputGbc = new GridBagConstraints();
        outputGbc.insets = new Insets(5, 5, 5, 5);
        outputGbc.fill = GridBagConstraints.HORIZONTAL;

        hammingCodeLabel = new JLabel("Hamming Kodu: ");
        styleOutputLabel(hammingCodeLabel);
        overallParityLabel = new JLabel("Toplam Parity (p0): ");
        styleOutputLabel(overallParityLabel);
        correctedCodeLabel = new JLabel("Düzeltilmiş Kod: ");
        styleOutputLabel(correctedCodeLabel);
        errorStatusLabel = new JLabel(" ");
        errorStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        errorStatusLabel.setForeground(new Color(55, 71, 79)); // #37474F
        bitPositionsLabel = new JLabel("Bit Pozisyonları: [p1, p2, d1, p4, d2, d3, d4, p8, d5, d6, d7, d8]");
        styleOutputLabel(bitPositionsLabel);

        outputGbc.gridx = 0;
        outputGbc.gridy = 0;
        outputGbc.gridwidth = 2;
        outputPanel.add(hammingCodeLabel, outputGbc);
        outputGbc.gridy = 1;
        outputPanel.add(overallParityLabel, outputGbc);
        outputGbc.gridy = 2;
        outputPanel.add(correctedCodeLabel, outputGbc);
        outputGbc.gridy = 3;
        outputPanel.add(errorStatusLabel, outputGbc);
        outputGbc.gridy = 4;
        outputPanel.add(bitPositionsLabel, outputGbc);

        // Merkez panele ekle
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(inputPanel, gbc);
        gbc.gridx = 1;
        centerPanel.add(outputPanel, gbc);

        // Açıklama paneli
        JPanel descriptionContainer = new JPanel(new BorderLayout());
        descriptionContainer.setOpaque(false);
        JButton toggleDescriptionButton = new JButton("Uygulama Detayı İçin Tıklayın  ▼ ");
        styleButton(toggleDescriptionButton);
        descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(new Color(227, 242, 253)); // #E3F2FD
        descriptionPanel.setBorder(BorderFactory.createLineBorder(new Color(2, 136, 209), 2));
        JTextArea description = new JTextArea(
            "Bu uygulama, Hamming SEC-DED (Single Error Correction - Double Error Detection) simülatörüdür. " +
            "Kullanıcılar, 8, 16 veya 32 bitlik veri girişi yaparak Hamming kodu oluşturabilir, rastgele veya belirli bir pozisyonda hata ekleyebilir, " +
            "ve bu hataları tespit edip düzeltebilir. SEC-DED, Hamming koduna eklenen bir genel parite biti ile tek bitlik hataları düzeltebilir ve " +
            "çift bitlik hataları algılayabilir. Bu simülatör, veri iletişiminde hata tespiti ve düzeltme süreçlerini görselleştirmek için tasarlanmıştır."
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setEditable(false);
        description.setBackground(new Color(227, 242, 253));
        description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        description.setLineWrap(true); // Metni otomatik satır sonuna sığdır
        description.setWrapStyleWord(true); // Kelimeleri bölmeden satır sonuna sığdır
        descriptionPanel.add(description, BorderLayout.CENTER);
        descriptionPanel.setVisible(false);
        descriptionContainer.add(toggleDescriptionButton, BorderLayout.NORTH);
        descriptionContainer.add(descriptionPanel, BorderLayout.CENTER);

        toggleDescriptionButton.addActionListener(e -> {
            descriptionVisible = !descriptionVisible;
            descriptionPanel.setVisible(descriptionVisible);    
            toggleDescriptionButton.setText(descriptionVisible ? "Uygulama Detayı İçin Tıklayın  ▲  " : "Uygulama Detayı İçin Tıklayın  ▼ ");
            frame.revalidate();
        });

        // Ana panele ekle
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(descriptionContainer, BorderLayout.SOUTH);

        // Buton aksiyonları
        codeButton.addActionListener(e -> {
            String data = dataInput.getText().trim();
            int bitLength = Integer.parseInt(bitLengthCombo.getSelectedItem().toString().split(" ")[0]);
            if (!HammingCode.isValidBinary(data, bitLength)) {
                showErrorMessage("Lütfen tam olarak " + bitLength + " bitlik 0/1 dizisi girin!");
                return;
            }
            String hammingCode = HammingCode.calculateHammingCode(data);
            hammingCodeLabel.setText("Hamming Kodu: " + hammingCode);
            overallParityLabel.setText("Toplam Parity (p0): " + hammingCode.charAt(hammingCode.length() - 1));
            correctedCodeLabel.setText("Düzeltilmiş Kod: ");
            errorStatusLabel.setText("Kod oluşturuldu, hata yok.");
            updateBitPositions(bitLength);
        });

        randomErrorButton.addActionListener(e -> {
            String code = hammingCodeLabel.getText().replace("Hamming Kodu: ", "");
            if (code.isEmpty()) {
                showErrorMessage("Önce kod oluşturun!");
                return;
            }
            String[] result = HammingCode.introduceRandomError(code).split(",");
            hammingCodeLabel.setText("Hamming Kodu: " + result[0]);
            errorStatusLabel.setText(result[1] + ". bitten rastgele hata oluşturuldu.");
            correctedCodeLabel.setText("Düzeltilmiş Kod: ");
        });

        errorAtPosButton.addActionListener(e -> {
            String code = hammingCodeLabel.getText().replace("Hamming Kodu: ", "");
            if (code.isEmpty()) {
                showErrorMessage("Önce kod oluşturun!");
                return;
            }
            Integer pos = (Integer) errorPosCombo.getSelectedItem();
            if (pos == null) {
                showErrorMessage("Hata pozisyonu seçin!");
                return;
            }
            String[] result = HammingCode.introduceErrorAtPosition(code, pos).split(",");
            hammingCodeLabel.setText("Hamming Kodu: " + result[0]);
            errorStatusLabel.setText(result[1] + ". bitten hata oluşturuldu.");
            correctedCodeLabel.setText("Düzeltilmiş Kod: ");
        });

        checkErrorButton.addActionListener(e -> {
            String code = hammingCodeLabel.getText().replace("Hamming Kodu: ", "");
            if (code.isEmpty()) {
                showErrorMessage("Önce kod oluşturun!");
                return;
            }
            String[] result = HammingCode.detectAndCorrectError(code, dataInput.getText().trim());
            errorStatusLabel.setText(result[0]);
            correctedCodeLabel.setText("Düzeltilmiş Kod: " + result[1]);
            overallParityLabel.setText("Toplam Parity (p0): " + result[2]);
        });

        // İlk hata pozisyonlarını güncelle
        updateErrorPositions();
        frame.setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(38, 198, 218)); // #26C6DA
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 172, 193)); // #00ACC1
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(38, 198, 218));
            }
        });
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(55, 71, 79)); // #37474F
    }

    private void styleOutputLabel(JLabel label) {
        label.setFont(new Font("Consolas", Font.BOLD, 18));
        label.setForeground(new Color(55, 71, 79));
    }

    private void showErrorMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(frame, "Hata");
        dialog.getContentPane().setBackground(new Color(255, 112, 67)); // #FF7043
        dialog.setVisible(true);
    }

    private void updateErrorPositions() {
        int bitLength = Integer.parseInt(bitLengthCombo.getSelectedItem().toString().split(" ")[0]);
        dataInput.setText("");
        dataInput.requestFocus();
        errorPosCombo.removeAllItems();
        errorPosCombo.addItem(null); // Varsayılan olarak hiçbir şey seçili olmasın
        int totalBits = HammingCode.getTotalLength(bitLength);
        for (int i = 1; i <= totalBits; i++) {
            errorPosCombo.addItem(i);
        }
        errorPosCombo.setSelectedItem(null); // Varsayılan olarak hiçbir pozisyon seçili olsun
        updateBitPositions(bitLength);
    }

    private void updateBitPositions(int bitLength) {
        StringBuilder positions = new StringBuilder("Bit Pozisyonları: [<br>");
        int totalBits = HammingCode.getTotalLength(bitLength);
        int dataIndex = 1;
        int counter = 0;

        for (int i = 1; i <= totalBits; i++) {
            if (HammingCode.isPowerOfTwo(i)) {
                positions.append("p").append(i);
            } else if (i == totalBits) {
                positions.append("p0");
            } else {
                positions.append("d").append(dataIndex++);
            }
            counter++;
            if (i < totalBits) positions.append(", ");
            // Her 8 elemandan sonra alt satıra geç
            if (counter % 8 == 0 && i < totalBits) {
                positions.append("<br>");
            }
        }
        positions.append("]");
        bitPositionsLabel.setText("<html>" + positions.toString() + "</html>");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}