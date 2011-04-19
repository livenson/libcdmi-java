package eu.venusc.cdmi;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

abstract class CommonBodyElements implements JSONStreamAware {

	String objectURI;
	String objectID;
	String parentURI;
	String domainURI;
	String mimetype;
	MetadataField metadata;
}

class BlobCreateRequest extends CommonBodyElements {

	String deserialize;
	String serialize;
	String copy;
	String move;
	String reference;
	String value;

	public BlobCreateRequest() {
	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		if (objectURI != null)
			obj.put("objectURI", objectURI);
		if (objectID != null)
			obj.put("objectID", objectID);
		if (parentURI != null)
			obj.put("parentURI", parentURI);
		if (domainURI != null)
			obj.put("domainURI", domainURI);
		if (mimetype != null)
			obj.put("mimetype", mimetype);
		if (metadata != null)
			obj.put("metadata", metadata);
		if (deserialize != null)
			obj.put("deserialize", deserialize);
		if (serialize != null)
			obj.put("serialize", serialize);
		if (copy != null)
			obj.put("copy", copy);
		if (reference != null)
			obj.put("reference", reference);
		if (value != null)
			obj.put("value", value);
		JSONValue.writeJSONString(obj, out);
	}

}

class BlobCreateResponse extends CommonBodyElements {

	String capabilitiesURI;
	String completionStatus;
	String percentComplete;

	public BlobCreateResponse() {

	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		if (objectURI != null)
			obj.put("objectURI", objectURI);
		if (objectID != null)
			obj.put("objectID", objectID);
		if (parentURI != null)
			obj.put("parentURI", parentURI);
		if (domainURI != null)
			obj.put("domainURI", domainURI);
		if (mimetype != null)
			obj.put("mimetype", mimetype);
		if (metadata != null)
			obj.put("metadata", metadata);
		if (capabilitiesURI != null)
			obj.put("metadata", metadata);
		if (completionStatus != null)
			obj.put("completionStatus", completionStatus);
		if (percentComplete != null)
			obj.put("percentComplete", percentComplete);
		JSONValue.writeJSONString(obj, out);
	}

}

class BlobReadResponse extends CommonBodyElements {

	String capabilitiesURI;
	String completionStatus;
	String percentComplete;
	String valuerange;
	String value;

	public BlobReadResponse() {

	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		if (objectURI != null)
			obj.put("objectURI", objectURI);
		if (objectID != null)
			obj.put("objectID", objectID);
		if (parentURI != null)
			obj.put("parentURI", parentURI);
		if (domainURI != null)
			obj.put("domainURI", domainURI);
		if (mimetype != null)
			obj.put("mimetype", mimetype);
		if (metadata != null)
			obj.put("metadata", metadata);
		if (capabilitiesURI != null)
			obj.put("metadata", metadata);
		if (completionStatus != null)
			obj.put("completionStatus", completionStatus);
		if (percentComplete != null)
			obj.put("percentComplete", percentComplete);
		if (valuerange != null)
			obj.put("valuerange", valuerange);
		if (value != null)
			obj.put("value", value);
		JSONValue.writeJSONString(obj, out);
	}

}

class ContainerReadRequest extends CommonBodyElements {

	String capabilitiesURI;
	String completionStatus;
	String percentComplete;
	String exports;
	String snapshots;
	String childrenrange;
	String[] children;

	public ContainerReadRequest() {
	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();

		if (objectURI != null)
			obj.put("objectURI", objectURI);
		if (objectID != null)
			obj.put("objectID", objectID);
		if (parentURI != null)
			obj.put("parentURI", parentURI);
		if (domainURI != null)
			obj.put("domainURI", domainURI);
		if (mimetype != null)
			obj.put("mimetype", mimetype);
		if (metadata != null)
			obj.put("metadata", metadata);
		if (capabilitiesURI != null)
			obj.put("capabilitiesURI", capabilitiesURI);
		if (completionStatus != null)
			obj.put("completionStatus", completionStatus);
		if (percentComplete != null)
			obj.put("percentComplete", percentComplete);
		if (exports != null)
			obj.put("exports", exports);
		if (snapshots != null)
			obj.put("snapshots", snapshots);
		if (childrenrange != null)
			obj.put("childrenrange", childrenrange);
		if (children != null)
			obj.put("children", children);
		JSONValue.writeJSONString(obj, out);
	}

}

class ContainerCreateRequest extends CommonBodyElements {
	public ContainerCreateRequest() {
	}

	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj = new LinkedHashMap();
		if (objectURI != null)
			obj.put("objectURI", objectURI);
		if (objectID != null)
			obj.put("objectID", objectID);
		if (parentURI != null)
			obj.put("parentURI", parentURI);
		if (domainURI != null)
			obj.put("domainURI", domainURI);
		if (mimetype != null)
			obj.put("mimetype", mimetype);
		if (metadata != null)
			obj.put("metadata", metadata);

		JSONValue.writeJSONString(obj, out);
	}
}

class MetadataField implements JSONAware {

	int cdmi_size;
	String cdmi_mtime;
	String cdmi_atime;
	String cdmi_ctime;

	public MetadataField() {
	}

	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("cdmi_size", cdmi_size);
		obj.put("cdmi_mtime", cdmi_mtime);
		obj.put("cdmi_atime", cdmi_atime);
		obj.put("cdmi_ctime", cdmi_ctime);
		return obj.toString();
	}

}
