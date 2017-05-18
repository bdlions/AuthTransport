
import com.auction.dto.Credential;
import java.net.InetAddress;
import java.util.ArrayList;
import org.bdlions.session.ISession;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.channel.exceptions.ChannelOpenedException;
import org.bdlions.transport.channel.provider.ChannelProviderImpl;
import org.bdlions.transport.packet.IPacket;
import org.bdlions.util.handler.request.IClientRequestHandler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alamgir
 */
public class ChannelImplementationTest {
    public static void main(String[] args) {
        try {
            ChannelProviderImpl impl = new ChannelProviderImpl(new IClientRequestHandler() {
                @Override
                public Object executeRequest(IPacket packet) throws Exception, Throwable {
                    System.out.println("Execute.....");
                    return new Object();
                }
            }, new ISessionManager() {
                @Override
                public ISession getSessionBySessionId(String sessionId) {
                    return null;
                }

                @Override
                public ISession getOnlineSessionByUserId(long userId) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public ISession createSession(Credential iSession) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void destroySession(String sessionId) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void updateSession(String sessionId, InetAddress address, int port) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public ISession getSession(String userName, int device) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public ISession getLatestSession(String userName) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public ArrayList<ISession> getSessions(String userName) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            impl.start();

        } catch (ChannelOpenedException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
