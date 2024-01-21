import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

class Catatan {
    private String judul;
    private String isi;

    public Catatan(String judul, String isi) {
        this.judul = judul;
        this.isi = isi;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        return isi;
    }
}

class CatatanApp {
    private ArrayList<Catatan> catatanList;
    private JFrame frame;
    private JList<String> catatanJList;
    private DefaultListModel<String> listModel;
    private JTextField judulField;
    private JTextArea isiArea;

    public CatatanApp() {
        catatanList = new ArrayList<>();
        loadCatatanFromFile();

        frame = new JFrame("Aplikasi Catatan");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        listModel = new DefaultListModel<>();
        catatanJList = new JList<>(listModel);
        catatanJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catatanJList.addListSelectionListener(e -> showCatatanDetail());

        JScrollPane listScrollPane = new JScrollPane(catatanJList);

        judulField = new JTextField();
        isiArea = new JTextArea();
        JButton addButton = new JButton("Tambah");
        JButton updateButton = new JButton("Perbarui");
        JButton deleteButton = new JButton("Hapus");

        addButton.addActionListener(e -> tambahCatatan());
        updateButton.addActionListener(e -> perbaruiCatatan());
        deleteButton.addActionListener(e -> hapusCatatan());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        frame.setLayout(new BorderLayout());
        frame.add(listScrollPane, BorderLayout.WEST);
        frame.add(createDetailPanel(), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateCatatanList();
        frame.setVisible(true);
    }

    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(3, 2));
        detailPanel.add(new JLabel("Judul:"));
        detailPanel.add(judulField);
        detailPanel.add(new JLabel("Isi:"));
        detailPanel.add(new JScrollPane(isiArea));
        return detailPanel;
    }

    private void showCatatanDetail() {
        int selectedIndex = catatanJList.getSelectedIndex();
        if (selectedIndex != -1) {
            Catatan selectedCatatan = catatanList.get(selectedIndex);
            judulField.setText(selectedCatatan.getJudul());
            isiArea.setText(selectedCatatan.getIsi());
        }
    }

    private void tambahCatatan() {
        String judul = judulField.getText();
        String isi = isiArea.getText();
        if (!judul.isEmpty() && !isi.isEmpty()) {
            Catatan newCatatan = new Catatan(judul, isi);
            catatanList.add(newCatatan);
            updateCatatanList();
            saveCatatanToFile();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(frame, "Judul dan Isi tidak boleh kosong");
        }
    }

    private void perbaruiCatatan() {
        int selectedIndex = catatanJList.getSelectedIndex();
        if (selectedIndex != -1) {
            Catatan selectedCatatan = catatanList.get(selectedIndex);
            selectedCatatan = new Catatan(judulField.getText(), isiArea.getText());
            catatanList.set(selectedIndex, selectedCatatan);
            updateCatatanList();
            saveCatatanToFile();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(frame, "Pilih catatan yang akan diperbarui");
        }
    }

    private void hapusCatatan() {
        int selectedIndex = catatanJList.getSelectedIndex();
        if (selectedIndex != -1) {
            catatanList.remove(selectedIndex);
            updateCatatanList();
            saveCatatanToFile();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(frame, "Pilih catatan yang akan dihapus");
        }
    }

    private void updateCatatanList() {
        listModel.clear();
        for (Catatan catatan : catatanList) {
            listModel.addElement(catatan.getJudul());
        }
    }

    private void clearForm() {
        judulField.setText("");
        isiArea.setText("");
    }

    private void loadCatatanFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("catatan.dat"))) {
            catatanList = (ArrayList<Catatan>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveCatatanToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("catatan.dat"))) {
            oos.writeObject(catatanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CatatanApp::new);
    }
}
