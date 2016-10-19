package http2xp.jetty;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.stereotype.Component;

/**
 * This {@link EmbeddedServletContainerCustomizer} will customize embedded Jetty
 * configuration to:
 * <ul>
 *     <li>update the SSLContextFactory to select the appropriate TLS cipher for HTTP/2 using {@code HTTP2Cipher.COMPARATOR}
 *     <li>replace the ConnectionFactories configured by Boot by {@link ALPNServerConnectionFactory} and {@link HTTP2ServerConnectionFactory}
 * </ul>
 * @author Brian Clozel
 */

@Component
public class JettyHttp2Customizer implements EmbeddedServletContainerCustomizer {

    private final ServerProperties serverProperties;

    @Autowired
    public JettyHttp2Customizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        JettyEmbeddedServletContainerFactory factory = (JettyEmbeddedServletContainerFactory) container;

        factory.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(Server server) {
                if (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) {
                    ServerConnector connector = (ServerConnector) server.getConnectors()[0];
                    int port = connector.getPort();
                    HttpConfiguration httpConfiguration = connector
                            .getConnectionFactory(HttpConnectionFactory.class).getHttpConfiguration();
                    ConnectionFactory[] connectionFactories = createConnectionFactories(httpConfiguration);

                    ServerConnector serverConnector = new ServerConnector(server, connectionFactories);
                    serverConnector.setPort(port);
                    server.setConnectors(new Connector[]{serverConnector});
                }
            }

            private ConnectionFactory[] createConnectionFactories(HttpConfiguration httpConfiguration) {

                HTTP2ServerConnectionFactory http2ServerConnectionFactory =
                        new HTTP2ServerConnectionFactory(httpConfiguration);

                return new ConnectionFactory[]{http2ServerConnectionFactory};
            }
        });
    }
}
