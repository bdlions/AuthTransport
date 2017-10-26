
import com.bdlions.util.ACTION;
import com.bdlions.util.REQUEST_TYPE;
import org.bdlions.transport.packet.IPacketHeader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alamgir
 */
public class MockPacketImpl implements IPacketHeader{

    private ACTION action = ACTION.SIGN_IN;
    private REQUEST_TYPE requestType = REQUEST_TYPE.AUTH;
    private String sessionId = "dfsdfsdf";
    private String packetId = "sfsdbfbb";
    private boolean isBroken = false;

    @Override
    public ACTION getAction() {
        return action;
    }

    @Override
    public REQUEST_TYPE getRequestType() {
        return requestType;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getPacketId() {
        return packetId;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
};
