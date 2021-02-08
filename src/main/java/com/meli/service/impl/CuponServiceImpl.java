package com.meli.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.meli.model.CuponRequest;
import com.meli.model.CuponResponse;
import com.meli.service.CuponService;

@Service
public class CuponServiceImpl implements CuponService {
	
	Logger logger = LoggerFactory.getLogger(CuponServiceImpl.class);
	
	/**
	 * Método que busca la mejor oferta para el cliente.
	 * @param CuponRequest
	 * @return Mejor oferta
	 */
	@Override
	public CuponResponse mejorOfertaCoupon(CuponRequest request) {
		CuponResponse response = new CuponResponse();
		Map<String, Float> itemsConPrecio = new LinkedHashMap<>();
		try {
			List<String> items = request.getItemIdList();
			for (String item : items) {
				Float price = getItemPrice(item);
				if (price != null && price <= request.getAmount()) {
					itemsConPrecio.put(item, price);
				}
			}

			if (itemsConPrecio.isEmpty()) {
				return null;
			}
			List<String> itemsMejorOferta = calculate(itemsConPrecio, request.getAmount());
			response.setItemsIdList(itemsMejorOferta);
			response.setTotal(total(itemsMejorOferta));
		} catch (Exception e) {
			logger.error("Error detectado al consultar el recurso mejorOfertaCoupon: " + e.getMessage());
		}
		return response;
	}

	/**
	 * Calcula la mejor oferta en base a una mapa de items únicos
	 * @param items
	 * @param amount
	 * @return lista de items
	 */
	private List<String> calculate(Map<String, Float> items, Float amount) {
		List<String> itemsId = new ArrayList<String>();
		List<Float> valores = new ArrayList<Float>();
		List<String> mejorOfertaEncontrada = new ArrayList<String>();
		List<Float> precios = new ArrayList<Float>();
		List<Entry<String, Float>> itemsOrdenados = new ArrayList<Map.Entry<String, Float>>();
		float aux = 0;
		itemsOrdenados = OrdenarMapa(items);

		itemsOrdenados.stream().forEach((map) -> {
			itemsId.add(map.getKey());
			valores.add(map.getValue());
		});

		float aux2 = 0;
		logger.info("------- Comienzo de analisis de mejor oferta ------- ");
		for (int x = 0; x < valores.size(); x++) {

			List<String> mo = new ArrayList<String>();
			mo.add(itemsId.get(x));
			aux = valores.get(x);
			for (int y = x + 1; y < valores.size(); y++) {
				if ((aux + valores.get(y)) <= amount) {
					aux += valores.get(y);
					mo.add(itemsId.get(y));
				} else {
					precios.add(aux);
					break;
				}
			}
			if (aux > aux2) {
				mejorOfertaEncontrada = mo;
				aux2 = aux;
			}

		}
		logger.info("Fin Analisis. \nMejor Oferta: " + mejorOfertaEncontrada);
		return mejorOfertaEncontrada;
	}

	/**
	 * Ordena un mapa de forma ascendente
	 * @param items
	 * @return
	 */
	private List<Entry<String, Float>> OrdenarMapa(Map<String, Float> items) {
		List<Map.Entry<String, Float>> students = new ArrayList<Map.Entry<String, Float>>();
		students.addAll(items.entrySet());

		Collections.sort(students, new Comparator<Map.Entry<String, Float>>() {
			@Override
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		return students;
	}

	/**
	 * Retorna el valor total de una lista de items calculados en {@link CuponServiceImpl.calculate}
	 * @param itemsC
	 * @return
	 */
	private Float total(List<String> itemsC) {
		Float precioTotal = (float) 0;
		for (String item : itemsC) {
			precioTotal = precioTotal + getItemPrice(item);
		}
		return precioTotal;
	}

	/**
	 * Busca en la api de meli el precio de un item
	 * @param item
	 * @return
	 */
	private Float getItemPrice(String item) {
		try {
			JSONObject json = null;
	        URL url = new URL("https://api.mercadolibre.com/items/" + item);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");

	        if (conn.getResponseCode() != 200) {
	          logger.error("La consulta a " + url.getPath() + " no trajo resultados.");
	          return null;
	        }

	        BufferedReader br = new BufferedReader(new InputStreamReader(
	            (conn.getInputStream())));

	        String jsonResponse = "";
	        while ((jsonResponse = br.readLine()) != null) {
	            logger.debug("respuesta obtenida en formato json " + jsonResponse);
	            jsonResponse.concat(jsonResponse);
	            json = new JSONObject(jsonResponse);
	        }
	         
	        float precioItem = json.getFloat("price");

	        conn.disconnect();
	        return precioItem;

	      } catch (MalformedURLException e) {
	    	  logger.error("Error al consultar los items: " + e.getMessage());
	      } catch (IOException e) {
	    	  logger.error("Error de lectura de json: " + e.getMessage());
	      }
		return null;
	}

}
