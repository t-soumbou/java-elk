package com.javacodegeeks.spring.elasticsearch.common;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Generic abstract class for basic Elasticsearch DAO
 * 
 * @author terrence
 *
 * @param <T>
 */
public abstract class GenericDAO<T> {

	private String entity;
	private String mapping;
	private String index;
	private Class<T> type;
	private final static String SEPARATOR = ":";
	private final static ObjectMapper mapper = new ObjectMapper();

	protected GenericDAO(String entity, String mapping, String index, Class<T> type) {
		this.entity = entity;
		this.mapping = mapping;
		this.index = index;
		this.type= type;
		createIndex(index);
	}

	/**
	 * calculate a doc key for given bean
	 * 
	 * @return the doc key format for given bean
	 */
	protected abstract String getKey(T bean);

	/**
	 * 
	 * @param bean
	 * @param response
	 * @return
	 */
	protected abstract T populateBean(T bean, GetResponse response);

	/**
	 * 
	 * @param bean
	 * @return
	 */
	protected abstract XContentBuilder entityToXbuilder(T bean);

	/**
	 * Get connection to Elastisearch
	 * 
	 * @return Connection to Elastisearch cluster
	 */
	private TransportClient getConnection() {
		return ClientProvider.getClient();
	}

	/**
	 * create index
	 * 
	 * @return
	 */
	private void createIndex(String index) {
		try {
			TransportClient client = getConnection();
			IndicesAdminClient indicesAdminClient = client.admin().indices();
			IndicesExistsResponse response = indicesAdminClient.prepareExists(index).get();
			if (!response.isExists())
				indicesAdminClient.prepareCreate(index).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads all bean from the database
	 * 
	 * @return all bean if found and loaded, null if not found
	 */
	protected List<T> doSelectAll() {
		List<T> list = new ArrayList<>();
		try (TransportClient client = getConnection()) {
			for (SearchHit searchHit : client.prepareSearch(index).get().getHits().getHits()) {
				String beanAsJson = searchHit.getSourceAsString();
				list.add(mapper.readValue(beanAsJson, type));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	/**
	 * Loads the given bean from the database using its primary key The given
	 * 
	 * @param bean
	 * @return bean if found and loaded, null if not found
	 */
	protected T doSelect(T bean) {
		try (TransportClient client = getConnection()) {
			String key = entity + SEPARATOR + getKey(bean);
			GetResponse response = client.prepareGet(index, mapping, key).get();
			String beanAsJson = response.getSourceAsString();
			 return mapper.readValue(beanAsJson, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Inserts the given bean in the database
	 * 
	 * @param bean
	 * @return elk code Indicates if the insert is successful
	 */
	protected byte doInsert(T bean) {
		byte res;
		try (TransportClient client = getConnection()) {
			String key = entity + SEPARATOR + getKey(bean);
			GetResponse check = client.prepareGet(index, mapping, key).get();
			if (check.isExists())
				throw new RuntimeException("this key already exist");
			XContentBuilder builder = entityToXbuilder(bean);
			IndexResponse response = client.prepareIndex(index, mapping, key).setSource(builder).get();
			res = response.getResult().getOp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	/**
	 * Updates the given bean in the database
	 * 
	 * @param bean
	 * @return the elk return code (i.e. the row count affected by the UPDATE
	 */
	protected byte doUpdate(T bean) {
		try (TransportClient client = getConnection()) {
			String key = entity + SEPARATOR + getKey(bean);
			XContentBuilder builder = entityToXbuilder(bean);
			UpdateRequest updateRequest = new UpdateRequest(index, mapping, key).doc(builder);
			UpdateResponse response = client.update(updateRequest).get();
			return response.getResult().getOp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes the given bean in the database
	 * 
	 * @param bean
	 * @return the elk return code (i.e. the row count affected by the DELETE
	 *         operation )
	 */
	protected byte doDelete(T bean) {
		try (TransportClient client = getConnection()) {
			String key = entity + SEPARATOR + getKey(bean);
			DeleteResponse response = client.prepareDelete(index, mapping, key).get();
			return response.getResult().getOp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks if the given bean exists in the database
	 * 
	 * @param bean
	 * @return true if bean exist false else
	 */
	protected boolean doExists(T bean) {
		try (TransportClient client = getConnection()) {
			String key = entity + SEPARATOR + getKey(bean);
			GetResponse response = client.prepareGet(index, mapping, key).get();
			return response.isExists();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Counts all the occurrences in the table
	 * 
	 * @return
	 */
	protected long doCountAll() {
		try (TransportClient client = getConnection()) {
			return client.prepareSearch(index).get().getHits().getTotalHits();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String buildString(Object... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(args[0].toString());
		for (int i = 1; i < args.length; i++) {
			sb.append("|").append(args[i].toString());
		}
		return sb.toString();
	}

}
