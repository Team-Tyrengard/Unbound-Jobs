package com.tyrengard.unbound.jobs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tyrengard.aureycore.common.struct.UUIDDataType;
import com.tyrengard.unbound.jobs.workers.Worker;
import com.tyrengard.unbound.jobs.workers.WorkerManager;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

class Utils {

    static TimeZone getTimeZoneOfPlayer(Player p) {
        InetSocketAddress inetSocketAddress = p.getAddress();
        if (inetSocketAddress != null) {
            String ipAddress = inetSocketAddress.getHostString();

            HttpRequest request = HttpRequest.newBuilder(URI.create("http://ipwhois.app/json/" + ipAddress)).build();
            HttpClient httpClient = HttpClient.newHttpClient();
            try {
                Map<String, Object> response = new Gson().fromJson(
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                if ((boolean) response.get("success")) {
                    return TimeZone.getTimeZone((String) response.get("timezone"));
                } else return null;
            } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }
}
