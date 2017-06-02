package net;

public class Message {

    private String msg_data;
    // Types of messages: Request Block Chain(RQBC), BlockChain Response(BCRS), 
    // BlockResponse (BKRS), No Blocks Needed(NBKN), Broadcast(BCST), Transaction (TX).
    public enum Message_Type { RQBC, BCRS, BKRS, NBKN, BCST, TX };
    private final Message_Type msg_type;
    
    /**
     * Creates a message based off of a raw message string.
     * @param raw_msg          The raw data being transmitted.
     */
    public Message(String raw_msg) {
        // Split message components.
        String[] msg_comps = raw_msg.split(";");
        this.msg_type = Message_Type.valueOf(msg_comps[0]);
        
        try {
            this.msg_data = msg_comps[1];
        } catch (ArrayIndexOutOfBoundsException AIBE) {
            this.msg_data = "";
        }
    }
    
    /**
     * Creates a message with a specific type.
     * @param type              The message type for this message.
     * @param raw_data          Raw data being transmitted.
     */
    public Message(String type, String raw_data) {
        this.msg_data = raw_data;
        this.msg_type = Message_Type.valueOf(type);
    }
    
    @Override
    public String toString() {
        return msg_type + ";" + msg_data;
    }
    
    /**
     * @return                  The raw data held in this message.
     */
    public String getRawData() {
        return this.msg_data;
    }
    
    /**
     * @return                  The type of this message.
     */
    public String getType() {
        return msg_type.toString();
    }
}