package mqtt.persistence;

import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import java.io.*;

public class MqttFilePersistence
    implements MqttPersistence
{

    private String directoryPath;
    private File abstractSentDirPath;
    private File abstractRcvdDirPath;
    private String updEnding;
    private String sentFileNamesRestored[];
    private String rcvdFileNamesRestored[];

    public MqttFilePersistence()
    {
        this("");
    }

    public MqttFilePersistence(String s)
    {
        directoryPath = "";
        abstractSentDirPath = null;
        abstractRcvdDirPath = null;
        updEnding = "u";
        sentFileNamesRestored = null;
        rcvdFileNamesRestored = null;
        directoryPath = s;
    }

    public void open(String s, String s1)
        throws MqttPersistenceException
    {
        String s2 = "";
        if(s1.startsWith("local://"))
        {
            s2 = localConnectionToDirName(s1);
        } else
        if(s1.startsWith("tcp://"))
        {
            s2 = ipConnectionToDirName("tcp://".length(), s1);
        } else
        {
            throw new MqttPersistenceException("Unrecognised connection method:" + s1);
        }
        String s3 = s + File.separator + s2 + File.separator;
        if(directoryPath.equals(""))
        {
            abstractSentDirPath = new File(s3 + "sent");
            abstractRcvdDirPath = new File(s3 + "rcvd");
        } else
        {
            abstractSentDirPath = new File(directoryPath + File.separator + s3 + "sent");
            abstractRcvdDirPath = new File(directoryPath + File.separator + s3 + "rcvd");
        }
        if(!abstractSentDirPath.exists() && !abstractSentDirPath.mkdirs())
        {
            throw new MqttPersistenceException("open: Failed to create directory " + abstractSentDirPath.getAbsolutePath());
        }
        if(!abstractRcvdDirPath.exists() && !abstractRcvdDirPath.mkdirs())
        {
            throw new MqttPersistenceException("open: Failed to create directory " + abstractRcvdDirPath.getAbsolutePath());
        } else
        {
            return;
        }
    }

    private String localConnectionToDirName(String s)
    {
        return s.substring("local://".length()) + "_0";
    }

    private String ipConnectionToDirName(int i, String s)
    {
        int j = s.indexOf(':');
        j = s.indexOf(':', j + 1);
        int k = s.indexOf(',');
        String s1 = s.substring(i, j);
        String s2 = null;
        if(k < 0)
        {
            s2 = s.substring(j + 1);
        } else
        {
            s2 = s.substring(j + 1, k);
        }
        return s1 + "_" + s2;
    }

    public void close()
    {
    }

    public void reset()
        throws MqttPersistenceException
    {
        deleteLogFiles(abstractSentDirPath);
        deleteLogFiles(abstractRcvdDirPath);
    }

    private void deleteLogFiles(File file)
    {
        Object obj = null;
        String as[] = file.list();
        if(as != null)
        {
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(file, as[i]);
                file1.delete();
            }

        }
    }

    public byte[][] getAllSentMessages()
        throws MqttPersistenceException
    {
        byte abyte0[][] = (byte[][])null;
        File file = null;
        sentFileNamesRestored = abstractSentDirPath.list();
        if(sentFileNamesRestored != null)
        {
            for(int i = 0; i < sentFileNamesRestored.length; i++)
            {
                file = new File(abstractSentDirPath, sentFileNamesRestored[i]);
                String s = file.getAbsolutePath();
                if(!s.endsWith(updEnding))
                {
                    continue;
                }
                File file1 = new File(s.substring(0, s.length() - updEnding.length()));
                if(file1.exists() && !file1.delete())
                {
                    throw new MqttPersistenceException("getAllSentMessages: Failed to delete file:" + s);
                }
            }

            sentFileNamesRestored = abstractSentDirPath.list();
            abyte0 = new byte[sentFileNamesRestored.length][];
            for(int j = 0; j < sentFileNamesRestored.length; j++)
            {
                try
                {
                    file = new File(abstractSentDirPath, sentFileNamesRestored[j]);
                    FileInputStream fileinputstream = new FileInputStream(file);
                    byte abyte1[] = new byte[fileinputstream.available()];
                    fileinputstream.read(abyte1);
                    abyte0[j] = abyte1;
                    fileinputstream.close();
                }
                catch(IOException ioexception)
                {
                    throw new MqttPersistenceException("getAllSentMessages: Failed to read file " + file.getAbsolutePath());
                }
            }

        }
        return abyte0;
    }

    public byte[][] getAllReceivedMessages()
        throws MqttPersistenceException
    {
        byte abyte0[][] = (byte[][])null;
        File file = null;
        rcvdFileNamesRestored = abstractRcvdDirPath.list();
        if(rcvdFileNamesRestored != null)
        {
            abyte0 = new byte[rcvdFileNamesRestored.length][];
            for(int i = 0; i < rcvdFileNamesRestored.length; i++)
            {
                try
                {
                    file = new File(abstractRcvdDirPath, rcvdFileNamesRestored[i]);
                    FileInputStream fileinputstream = new FileInputStream(file);
                    byte abyte1[] = new byte[fileinputstream.available()];
                    fileinputstream.read(abyte1);
                    abyte0[i] = abyte1;
                    fileinputstream.close();
                }
                catch(IOException ioexception)
                {
                    throw new MqttPersistenceException("getAllReceivedMessages: Failed to read file " + file.getAbsolutePath());
                }
            }

        }
        return abyte0;
    }

    public void addSentMessage(int i, byte abyte0[])
        throws MqttPersistenceException
    {
        File file = new File(abstractSentDirPath, (new Integer(i)).toString());
        if(file.exists())
        {
            file.delete();
        }
        FileOutputStream fileoutputstream;
        try
        {
            fileoutputstream = new FileOutputStream(file);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            throw new MqttPersistenceException("addSentMessage: FileNotFoundException - " + file.getAbsolutePath());
        }
        try
        {
            fileoutputstream.write(abyte0);
        }
        catch(IOException ioexception)
        {
            throw new MqttPersistenceException("addSentMessage: IOException writing to file " + file.getAbsolutePath());
        }
        try
        {
            fileoutputstream.close();
        }
        catch(IOException ioexception1) { }
    }

    public void updSentMessage(int i, byte abyte0[])
        throws MqttPersistenceException
    {
        String s = (new Integer(i)).toString();
        String s1 = (new Integer(i)).toString() + updEnding;
        File file = new File(abstractSentDirPath, s);
        File file1 = new File(abstractSentDirPath, s1);
        FileOutputStream fileoutputstream;
        try
        {
            fileoutputstream = new FileOutputStream(file1);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            throw new MqttPersistenceException("updSentMessage: FileNotFoundException - " + file1.getAbsolutePath());
        }
        try
        {
            fileoutputstream.write(abyte0);
        }
        catch(IOException ioexception)
        {
            throw new MqttPersistenceException("updSentMessage: IOException writing to file " + file1.getAbsolutePath());
        }
        try
        {
            fileoutputstream.close();
        }
        catch(IOException ioexception1) { }
        if(file.exists())
        {
            file.delete();
        }
    }

    public void delSentMessage(int i)
        throws MqttPersistenceException
    {
        String s = (new Integer(i)).toString();
        File file = new File(abstractSentDirPath, s);
        File file1 = new File(abstractSentDirPath, s + updEnding);
        if(file.exists())
        {
            file.delete();
        }
        if(file1.exists())
        {
            file1.delete();
        }
    }

    public void addReceivedMessage(int i, byte abyte0[])
        throws MqttPersistenceException
    {
        File file = new File(abstractRcvdDirPath, (new Integer(i)).toString());
        if(file.exists())
        {
            file.delete();
        }
        FileOutputStream fileoutputstream;
        try
        {
            fileoutputstream = new FileOutputStream(file);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            throw new MqttPersistenceException("addReceivedMessage: FileNotFoundException - " + file.getAbsolutePath());
        }
        try
        {
            fileoutputstream.write(abyte0);
        }
        catch(IOException ioexception)
        {
            throw new MqttPersistenceException("addReceivedMessage: IOException writing to file " + file.getAbsolutePath());
        }
        try
        {
            fileoutputstream.close();
        }
        catch(IOException ioexception1) { }
    }

    public void delReceivedMessage(int i)
        throws MqttPersistenceException
    {
        File file = new File(abstractRcvdDirPath, (new Integer(i)).toString());
        if(file.exists() && !file.delete())
        {
            throw new MqttPersistenceException("delReceivedMessage: Failed to delete file " + file.getAbsolutePath());
        } else
        {
            return;
        }
    }
}
