/*
 * BTC-e client
 *     Copyright (C) 2014  QuarkDev Solutions <quarkdev.solutions@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.QuarkLabs.BTCeClient;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class App {

    private AuthRequest mAuthRequest;

    public App(Context context) {
        if (mAuthRequest == null) {
            mAuthRequest = new AuthRequest(System.currentTimeMillis() / 1000L, context);
        }
    }

    /**
     * Gets info for provided pairs
     *
     * @param pairs
     * @return
     * @throws JSONException
     */
    public static JSONObject getPairInfo(String[] pairs) throws JSONException {
        String url = "https://btc-e.com/api/3/ticker/";
        for (String x : pairs) {
            url += x.replace("/", "_").toLowerCase() + "-";
        }
        SimpleRequest reqSim = new SimpleRequest();
        return reqSim.makeRequest(url.substring(0, url.length() - 1));
    }

    /**
     * Gets account info
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public JSONObject getAccountInfo() throws UnsupportedEncodingException, JSONException {

        JSONObject response;
        response = mAuthRequest.makeRequest("getInfo", null);
        return response;

    }

    /**
     * Gets history of transactions
     *
     * @param params
     * @return
     * @throws JSONException
     */
    public JSONObject getTransactionsHistory(Map<String, String> params) throws JSONException {
        JSONObject response = null;
        try {
            response = mAuthRequest.makeRequest("TransHistory", params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Gets history of trades
     *
     * @param params
     * @return
     * @throws JSONException
     */
    public JSONObject getTradeHistory(Map<String, String> params) throws JSONException {
        JSONObject response = null;
        try {
            response = mAuthRequest.makeRequest("TradeHistory", params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Gets active orders
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public JSONObject getActiveOrders() throws UnsupportedEncodingException, JSONException {


        return mAuthRequest.makeRequest("ActiveOrders", null);

    }

    /**
     * Makes trade request
     *
     * @param pair
     * @param type
     * @param rate
     * @param amount
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public JSONObject trade(String pair, String type, String rate, String amount) throws UnsupportedEncodingException, JSONException {

        HashMap<String, String> temp = new HashMap<>(4);
        temp.put("pair", "" + pair);
        temp.put("type", "" + type);
        temp.put("rate", "" + rate);
        temp.put("amount", "" + amount);

        return mAuthRequest.makeRequest("Trade", temp);

    }

    /**
     * Cancels order
     *
     * @param orderId
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public JSONObject cancelOrder(int orderId) throws UnsupportedEncodingException, JSONException {

        Map<String, String> temp = new HashMap<>(1);
        temp.put("order_id", String.valueOf(orderId));

        return mAuthRequest.makeRequest("CancelOrder", temp);

    }

}