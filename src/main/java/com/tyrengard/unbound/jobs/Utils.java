package com.tyrengard.unbound.jobs;

class Utils {

//    static TimeZone getTimeZoneOfPlayer(Player p) {
//        InetSocketAddress inetSocketAddress = p.getAddress();
//        if (inetSocketAddress != null) {
//            String ipAddress = inetSocketAddress.getHostString();
//
//            HttpRequest request = HttpRequest.newBuilder(URI.create("http://ipwhois.app/json/" + ipAddress)).build();
//            HttpClient httpClient = HttpClient.newHttpClient();
//            try {
//                Map<String, Object> response = new Gson().fromJson(
//                    httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body(), new TypeToken<HashMap<String, Object>>() {}.getType()
//                );
//                if ((boolean) response.get("success")) {
//                    return TimeZone.getTimeZone((String) response.get("timezone"));
//                } else return null;
//            } catch (IOException | InterruptedException exception) {
//                exception.printStackTrace();
//            }
//        }
//
//        return null;
//    }
}
