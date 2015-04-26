import java.io.*;
import javax.sound.sampled.*;

public class AudioThread extends Thread
{
    private static final int BUFFER_SIZE = 1024;
    private String fileName;

    public AudioThread(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File audioFile = new File(fileName);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(audioFile);
        } catch (IOException io) {}

        byte[] data = new byte[999999];
        try {
            int bytesRead = fileInputStream.read(data);
        } catch (IOException io) {
            System.out. println("1");
        }

        InputStream inputStream = new ByteArrayInputStream(data);
        try {
            AudioInputStream audioInputstream = AudioSystem.getAudioInputStream(inputStream);
            AudioFormat format = audioInputstream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(format);
            line.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = audioInputstream.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            line.drain();
            line.close();
            audioInputstream.close();
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("!!!!! UnsupportedAudioFileException in AudioThread !!!!!");
        } catch (LineUnavailableException ex) {
            System.out.println("!!!!! LineUnavailableException in AudioThread !!!!!");
        } catch (IOException ex) {
            System.out.println("!!!!! IOException in AudioThread !!!!!");
        }  
    }
}