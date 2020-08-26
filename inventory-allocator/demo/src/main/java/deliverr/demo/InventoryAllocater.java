
package deliverr.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Example resource class hosted at the URI path "/myresource"
 */
@Path("/inventoryallocater")
public class InventoryAllocater {

	/**
	 * Method processing HTTP POST requests, consuming "text/plain" MIME media type.
	 *  
	 * @return String that will be send back as a response of type "text/plain".
	 */
	@POST
	@Consumes("text/plain")
	public String getIt(String incoming) {

		System.out.println(" inout ---> " + incoming);
		// splitting incoming data into first input and second input

		String firstInput = "";
		String secondInput = "";
		try {
			firstInput = incoming.split("-")[0].trim();
			secondInput = incoming.split("-")[1].trim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return " provide valid input  " + "Input format:  first input – second input";

		}
		// to convert string to json object, we are using json library
		secondInput = "{'value' :" + secondInput + "}";
		JSONObject jsonObject = new JSONObject(secondInput);

		JSONObject jsonObject2 = new JSONObject(firstInput);

		// to maintain insertion order, we are using LinkedHashMap
		Map<String, Integer> input = new LinkedHashMap<String, Integer>();
		// input - a map of items that are being ordered
		try {
			for (String key : jsonObject2.keySet()) {
				input.put(key, (Integer) jsonObject2.get(key));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return " provide valid data in input  ";
		}
		// below code is to convert inventory data into map
		JSONArray value = (JSONArray) jsonObject.get("value");
		List<Object> list = value.toList();
		List<Object> iterator = list;
		Map<String, Map<String, Integer>> map = new LinkedHashMap<String, Map<String, Integer>>();
		try {
			for (Object object : iterator) {
				Map<String, Map<String, Integer>> map2 = (Map<String, Map<String, Integer>>) object;
				map.put(map2.get("name") + "", map2.get("inventory"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return " provide valid data in input  ";
		}

		// maintaining separate copy of input for processing - a map of items that are
		// being ordered
		Map<String, Integer> items = new LinkedHashMap<String, Integer>(input);
		// cartItems - to store selected items from warehouses
		Map<String, Map<String, Integer>> cartItems = new LinkedHashMap<String, Map<String, Integer>>();
		// missedItems - to store not available items
		Map<String, Integer> missedItems = new LinkedHashMap<String, Integer>();

		// below code is to iterate over input items, pick available items from
		// warehouses into cart items and add missing
		// items in missed Items map
		for (Map.Entry<String, Integer> entry : items.entrySet()) {
			String key = entry.getKey();
			Integer val = entry.getValue();
			int collectedItems = 0;
			for (Map.Entry<String, Map<String, Integer>> entry2 : map.entrySet()) {
				entry2.getKey();
				if (collectedItems == input.get(key)) {
					break;
				}
				Map<String, Integer> child = entry2.getValue();
				if (child.get(key) != null && val != 0) {
					if (child.get(key) >= val) {
						items.put(key, 0);

						Map<String, Integer> childCopy = cartItems.getOrDefault(entry2.getKey(),
								new LinkedHashMap<String, Integer>());
						childCopy.put(key, childCopy.getOrDefault(key, 0) + val);
						cartItems.put(entry2.getKey(), childCopy);
						collectedItems += val;

						child.put(key, child.get(key) - val);
						map.put(entry2.getKey(), child);
						val = 0;

					} else if (child.get(key) > 0) {
						Map<String, Integer> childCopy = cartItems.getOrDefault(entry2.getKey(),
								new LinkedHashMap<String, Integer>());
						childCopy.put(key, childCopy.getOrDefault(key, 0) + child.get(key));
						cartItems.put(entry2.getKey(), childCopy);
						collectedItems += child.get(key);

						items.put(key, val - child.get(key));
						val = val - child.get(key);
						child.put(key, 0);

						map.put(entry2.getKey(), child);

					}

				}
			}
			// Unavailable items will be stored in missed items
			if (val > 0) {
				missedItems.put(key, val);
			}
		}
		/*
		 * System.out.println(); System.out.println(cartItems + "   cart items");
		 * System.out.println(); System.out.println(missedItems + "   missed Items");
		 * System.out.println(); System.out.println(map + "   inventory");
		 */
		String missed = missedItems.size() > 0 ? "\nitems not available   " + missedItems : "";
		// response will be sent back to client
		String response = "input    " + input + "\noutput   " + cartItems + " \n....................................."
				+ "" + "\nstatus of warehouses  " + map + missed;

		response = response.replace("=", ":");

		return response;
	}
}
