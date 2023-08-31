package it.outset.t1_core.builders;

import java.net.URI;
import java.net.URISyntaxException;

import it.outset.t1_core.models.HostSettings;

public final class HostSettingsBuilder {
    private String protocol;
    private String host;
    private int portNumber;
    private int trackInterval;
    private int sendInterval;

    private HostSettingsBuilder(String protocol, String host) {
        this.protocol = protocol;
        this.host = host;
    }

    private HostSettingsBuilder(String url) {
        try {
            URI uri = new URI(url);
            this.protocol = uri.getHost();
            this.host = uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static HostSettingsBuilder newBuilder(String protocol, String host) {
        return new HostSettingsBuilder(protocol, host);
    }

    public static HostSettingsBuilder newBuilder(String url) {
        return new HostSettingsBuilder(url);
    }

    public HostSettingsBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * Numero di porta del servizio remoto.
     *
     * @param portNumber Numero porta 0-65535.
     * @return HostBuilder.
     */
    public HostSettingsBuilder portNumber(int portNumber) {
        if (portNumber < 0 || portNumber > 65535)
            throw new NumberFormatException("Wrong host port number.");
        this.portNumber = portNumber;
        return this;
    }

    /**
     * Intervallo di tempo di acquisizione della geolocalizzazione.
     *
     * @param trackInterval Tempo in secondi.
     * @return HostBuilder.
     */
    public HostSettingsBuilder trackInterval(int trackInterval) {
        this.trackInterval = trackInterval;
        return this;
    }

    /**
     * Intervallo di tempo di invio dei dati al servizio remoto.
     *
     * @param sendInterval Tempo in secondi.
     * @return HostBuilder.
     */
    public HostSettingsBuilder sendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
        return this;
    }

    public HostSettings build() {
        return new HostSettings(protocol, host, portNumber, trackInterval, sendInterval);
    }
}
