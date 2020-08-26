
package deliverr.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Example resource class hosted at the URI path "/myresource"
 */
@Path("/myresource")
public class MyResource {

	/**
	 * Method processing HTTP GET requests, producing "text/plain" MIME media type.
	 * 
	 * @return String that will be send back as a response of type "text/plain".
	 */
	@POST
	@Consumes("text/plain")
	public String getIt(String incoming) {

		System.out.println(" inout ---> " + incoming);

		String firstInput = incoming.split("-")[0].trim();
		String secondInput = incoming.split("-")[1].trim();
		secondInput = "{'value' :" + secondInput + "}";
		JSONObject jsonObject = new JSONObject(secondInput);

		JSONObject jsonObject2 = new JSONObject(firstInput);
		System.out.println(jsonObject2.keySet() + " secodn");
		Map<String, Integer> input = new LinkedHashMap<String, Integer>();
		for (String key : jsonObject2.keySet()) {
			input.put(key, (Integer) jsonObject2.get(key));
		}

		JSONArray value = (JSONArray) jsonObject.get("value");
		List<Object> list = value.toList();
		List<Object> iterator = list;
		Map<String, Map<String, Integer>> map = new LinkedHashMap<String, Map<String, Integer>>();
		for (Object object : iterator) {
			Map<String, Map<String, Integer>> map2 = (Map<String, Map<String, Integer>>) object;
			map.put(map2.get("name") + "", map2.get("inventory"));
		}

		System.out.println(map + "  main");

		System.out.println(input + " input");
		Map<String, Integer> items = new LinkedHashMap<String, Integer>(input);
		Map<String, Map<String, Integer>> cartItems = new LinkedHashMap<String, Map<String, Integer>>();
		Map<String, Integer> missedItems = new LinkedHashMap<String, Integer>();
		System.out.println(map + " inventory");
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
			if (val > 0) {
				missedItems.put(key, val);
			}
		}
		/*
		 * System.out.println(); System.out.println(cartItems + "   cart items");
		 * System.out.println(); System.out.println(missedItems + "   missed Items");
		 * System.out.println(); System.out.println(map + "   inventory");
		 */
		String missed= missedItems.size()>0? "\nitems not available   "+ missedItems:"";
		String response="input    " + input +"\noutput   "+ cartItems+ " \n....................................."
				+ ""+ "\nstatus of warehouses  "  + map +missed;
		
		response=response.replace("=", ":");

		return response;
	}
}
