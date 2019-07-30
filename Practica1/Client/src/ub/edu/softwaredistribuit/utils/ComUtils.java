package ub.edu.softwaredistribuit.utils;


import javafx.util.Pair;
import ub.edu.softwaredistribuit.commons.Card;
import ub.edu.softwaredistribuit.commons.Command;
import ub.edu.softwaredistribuit.commons.Rank;
import ub.edu.softwaredistribuit.commons.Suit;
import ub.edu.softwaredistribuit.commons.Winner;
import ub.edu.softwaredistribuit.exceptions.NoExistRankException;
import ub.edu.softwaredistribuit.exceptions.NoExistSuitException;
import ub.edu.softwaredistribuit.exceptions.NoExistWinnerException;
import ub.edu.softwaredistribuit.exceptions.UnknownActionException;

import java.io.*;
import java.net.Socket;

public class ComUtils {

    private final int STRSIZE = 20;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;

    public ComUtils(Socket socket) throws IOException {
        this.socket = socket;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dis = new DataInputStream(inputStream);
        dos = new DataOutputStream(outputStream);
    }


    /////////////
    //  WINNER //
    /////////////

    /**
     * Write Winner Command from certain Winner (TIE, SERVER, CLIENT)
     * @param winner Game Winner
     * @param currentBet Current bet in game
     * @throws IOException IOException Error
     */
    public void writeWinner(Winner winner, int currentBet) throws IOException {
        this.write_command(Command.WINS);
        this.write_space();
        this.writeChar((char) Character.forDigit(winner.ordinal(), 10));
        this.write_space();
        this.write_int32(currentBet);
    }

    /**
     * Read a Winner of the game and the current bet
     * @return Pair of Winner and currentBett in game
     * @throws IOException IOException Error
     * @throws NoExistWinnerException Error reading Winner. That winner does not exist!
     */
    public Pair<Winner,Integer> readWinner() throws IOException, NoExistWinnerException {
        this.read_space();
        String ord = this.readChar();
        Winner w = Winner.getWinnerFromString(ord);

        if(w == null){
            writeError("ERROR: The Winner [ " + ord + " ] does not exist!");
            throw new NoExistWinnerException(ord);
        }
        this.read_space();
        int bet = this.read_int32();

        return new Pair<>(w, bet);
    }

    ////////////
    //  ERROR //
    ////////////


    /**
     * Write Error Command with certain message
     * @param err Error message
     * @throws IOException IOException Error
     */
    public void writeError(String err) throws IOException {
        this.write_command(Command.ERRO);
        this.write_space();
        int msgLength = err.length();

        if (msgLength > 99) {
            System.out.println("Error! The error message is too long and will not be written!");
        }else{
            this.write_string_variable(2, err);
        }
    }

    /**
     * @return Error message
     * @throws IOException IOException Error
     */
    public String readError() throws IOException {
        String sp = this.read_space();
        String msg = this.read_string_variable(2);
        return sp + msg;
    }


    ////////////
    //  SUIT  //
    ////////////

    /**
     * Write a Suit's Card (sending a byte)
     * @param suit Card's Suit
     * @throws IOException IOException Error
     */
    public void writeSuit(Suit suit) throws IOException {
        writeChar(suit.getAscii());
    }

    /**
     * Read Suit's Card
     * @return Card's Suit
     * @throws IOException IOException Error
     * @throws NoExistSuitException Error reading Suit. That suit does not exist!
     */
    public Suit readSuit() throws IOException, NoExistSuitException {
                byte[] bStr = new byte[1];
        char cStr;

        bStr = read_bytes(1);
        cStr = (char) bStr[0];

        Suit s = Suit.getSuitFromAscii(cStr);

        if (s == null){
            writeError("ERROR: The Card Suit [ " + cStr + " ] does not exist!");
            throw new NoExistSuitException(cStr);
        }

        return s;
    }


    ////////////
    //  RANK  //
    ////////////


    /**
     * Write a Card's Rank (Sending a byte char)
     * @param rank Card's Rank
     * @throws IOException IOException Error
     */
    public void writeRank(Rank rank) throws IOException {
        writeChar(rank.getRank());
    }

    /**
     * Read Rank's Card
     * @return Card's Rank
     * @throws IOException IOException Error
     * @throws NoExistRankException Error reading Rank. That Rank does not exist!
     */
    public Rank readRank() throws IOException, NoExistRankException {

        byte[] bStr = new byte[1];
        char cStr;

        bStr = read_bytes(1);
        cStr = (char) bStr[0];

        Rank r = Rank.getRankFromChar(cStr);
        if (r == null){
            writeError("ERROR: The Card Rank [ " + cStr + " ] does not exist!");
            throw new NoExistRankException(cStr);
        }

        return r;
    }


    ////////////
    //  CARD  //
    ////////////

    /**
     * Write a Card to send
     * @param card Card to send
     * @throws IOException IOException Error
     */
    public void writeCard(Card card) throws IOException {
        write_space();
        writeRank(card.getRank());
        writeSuit(card.getSuit());
    }

    /**
     *
     * @return Read Card
     * @throws IOException IOException Error
     * @throws NoExistRankException Error reading Rank. That Rank does not exist!
     * @throws NoExistSuitException Error reading Suit. That Suit does not exist!
     */
    public Card readCard() throws IOException, NoExistRankException, NoExistSuitException {
        read_space();

        Rank r = this.readRank();
        Suit s = this.readSuit();

        return new Card(s, r);
    }


    ///////////////
    //  COMMAND  //
    ///////////////

    /**
     * Write a command to send from Command's
     * @param cmd Command to write (Send)
     * @throws IOException IOException Error
     */
    public void write_command(Command cmd) throws IOException {
        String scmd = cmd.name();
        int length = scmd.length();
        byte[] bStr = new byte[length];

        for (int i = 0; i < length; i++)
            bStr[i] = (byte) scmd.charAt(i);

        dos.write(bStr, 0, length);

    }

    /**
     * Write a command to send from Scanner String
     * @param cmd Command to write (Send)
     * @throws IOException IOException Error
     */
    public void write_command2(String cmd) throws IOException {
        int length = cmd.length();
        byte[] bStr = new byte[length];

        for (int i = 0; i < length; i++)
            bStr[i] = (byte) cmd.charAt(i);

        dos.write(bStr, 0, length);

    }

    /**
     * Read a command (Recived)
     * @return Read Command
     * @throws IOException IOException Error
     * @throws UnknownActionException Error reading command. That command does not exist!
     */
    public Command read_command() throws IOException, UnknownActionException {
        byte[] bStr = new byte[4];
        char[] cStr = new char[4];

        bStr = read_bytes(4);

        for (int i = 0; i < 4; i++)
            cStr[i] = (char) bStr[i];

        String result = String.valueOf(cStr);

        Command cmd = Command.getCommandFromString(result);

        if(cmd == null){
            this.writeError("ERROR: The taken action [ " + result + " ] is not known!");
            throw new UnknownActionException(result);
        }

        return cmd;

    }


    /////////////
    //  SPACE  //
    /////////////

    /**
     * Write a Space
     * @throws IOException IOException Error
     */
    public void write_space() throws IOException {
        this.writeChar(' ');
    }


    /**
     *
     * @return Read Space as String
     * @throws IOException IOException Error
     */
    public String read_space() throws IOException {
        return this.readChar();
    }

    ////////////
    //  CHAR  //
    ////////////

    /**
     * Write a char to send (byte)
     * @param c Char to write
     * @throws IOException IOException Error
     */
    public void writeChar(char c) throws IOException {
        byte[] bStr = new byte[1];
        bStr[0] = (byte) c;
        dos.write(bStr, 0, 1);
    }

    /**
     * Read a char
     * @return Read char as String
     * @throws IOException IOException Error
     */
    public String readChar() throws IOException {
        String str;
        byte[] bStr = new byte[1];
        char cStr;

        bStr = read_bytes(1);
        cStr = (char) bStr[0];
        str = String.valueOf(cStr);

        return str;
    }



    ////////////////////
    //  ALREADY DONE  //
    ////////////////////


    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
    }

    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dos.write(bytes, 0, 4);
    }

    public String read_string() throws IOException {
        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for (int i = 0; i < STRSIZE; i++)
            cStr[i] = (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    public void write_string(String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for (int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for (int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dos.write(bStr, 0, STRSIZE);
    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if (Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte) ((number >> 24) & 0xFF);
            bytes[1] = (byte) ((number >> 16) & 0xFF);
            bytes[2] = (byte) ((number >> 8) & 0xFF);
            bytes[3] = (byte) (number & 0xFF);
        } else {
            bytes[0] = (byte) (number & 0xFF);
            bytes[1] = (byte) ((number >> 8) & 0xFF);
            bytes[2] = (byte) ((number >> 16) & 0xFF);
            bytes[3] = (byte) ((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if (Endianness.BIG_ENNDIAN == endianness) {
            number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dis.read(bStr, len, numBytes - len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public String read_string_variable(int size) throws IOException {
        byte bHeader[] = new byte[size];
        char cHeader[] = new char[size];
        int numBytes = 0;

        // Llegim els bytes que indiquen la mida de l'string
        bHeader = read_bytes(size);
        // La mida de l'string ve en format text, per tant creem un string i el parsejem
        for (int i = 0; i < size; i++) {
            cHeader[i] = (char) bHeader[i];
        }
        numBytes = Integer.parseInt(new String(cHeader));

        // Llegim l'string
        byte bStr[] = new byte[numBytes];
        char cStr[] = new char[numBytes];
        bStr = read_bytes(numBytes);
        for (int i = 0; i < numBytes; i++)
            cStr[i] = (char) bStr[i];
        return String.valueOf(cStr);
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/
    public void write_string_variable(int size, String str) throws IOException {

        // Creem una seqüència amb la mida
        byte bHeader[] = new byte[size];
        String strHeader;
        int numBytes = 0;

        // Creem la capçalera amb el nombre de bytes que codifiquen la mida
        numBytes = str.length();

        strHeader = String.valueOf(numBytes);
        int len;
        if ((len = strHeader.length()) < size)
            for (int i = len; i < size; i++) {
                strHeader = "0" + strHeader;
            }
        for (int i = 0; i < size; i++)
            bHeader[i] = (byte) strHeader.charAt(i);
        // Enviem la capçalera
        dos.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dos.writeBytes(str);
    }

    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }
}


