/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.madhouse.ssp.avro;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class MediaBid extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -6461593908344743699L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"MediaBid\",\"namespace\":\"com.madhouse.ssp.avro\",\"fields\":[{\"name\":\"time\",\"type\":\"long\"},{\"name\":\"ua\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"ip\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"impid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"status\",\"type\":\"int\"},{\"name\":\"bidfloor\",\"type\":\"int\"},{\"name\":\"bidtype\",\"type\":\"int\"},{\"name\":\"location\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"request\",\"type\":{\"type\":\"record\",\"name\":\"MediaRequest\",\"fields\":[{\"name\":\"bid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"mediaid\",\"type\":\"long\",\"default\":0},{\"name\":\"category\",\"type\":\"int\",\"default\":0},{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"bundle\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"adspaceid\",\"type\":\"long\",\"default\":0},{\"name\":\"adspacekey\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"adtype\",\"type\":\"int\"},{\"name\":\"w\",\"type\":\"int\"},{\"name\":\"h\",\"type\":\"int\"},{\"name\":\"did\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"didmd5\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"dpid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"dpidmd5\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"mac\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"macmd5\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"ifa\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"carrier\",\"type\":\"int\",\"default\":0},{\"name\":\"connectiontype\",\"type\":\"int\",\"default\":0},{\"name\":\"devicetype\",\"type\":\"int\",\"default\":0},{\"name\":\"dealid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"geo\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Geo\",\"fields\":[{\"name\":\"type\",\"type\":\"int\",\"default\":0},{\"name\":\"lat\",\"type\":\"float\",\"default\":0},{\"name\":\"lon\",\"type\":\"float\",\"default\":0}]}],\"default\":null},{\"name\":\"os\",\"type\":\"int\",\"default\":0},{\"name\":\"osv\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"ip\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"ua\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"make\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"model\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"cell\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"cellmd5\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"tags\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"default\":null},{\"name\":\"test\",\"type\":\"int\",\"default\":0},{\"name\":\"bidfloor\",\"type\":\"int\",\"default\":0}]}},{\"name\":\"response\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"MediaResponse\",\"fields\":[{\"name\":\"dspid\",\"type\":\"long\"},{\"name\":\"cid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"crid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"admid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"layout\",\"type\":\"int\",\"default\":0},{\"name\":\"icon\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"cover\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"title\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"desc\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"duration\",\"type\":\"int\",\"default\":0},{\"name\":\"adm\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"dealid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"lpgurl\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"acttype\",\"type\":\"int\",\"default\":1},{\"name\":\"monitor\",\"type\":{\"type\":\"record\",\"name\":\"Monitor\",\"fields\":[{\"name\":\"impurl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Track\",\"fields\":[{\"name\":\"startdelay\",\"type\":\"int\",\"default\":0},{\"name\":\"url\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}},\"default\":null},{\"name\":\"clkurl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"default\":null},{\"name\":\"securl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"default\":null},{\"name\":\"exts\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"default\":null}]}}]}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<MediaBid> ENCODER =
      new BinaryMessageEncoder<MediaBid>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<MediaBid> DECODER =
      new BinaryMessageDecoder<MediaBid>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<MediaBid> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<MediaBid> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<MediaBid>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this MediaBid to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a MediaBid from a ByteBuffer. */
  public static MediaBid fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long time;
  @Deprecated public java.lang.String ua;
  @Deprecated public java.lang.String ip;
  @Deprecated public java.lang.String impid;
  @Deprecated public int status;
  @Deprecated public int bidfloor;
  @Deprecated public int bidtype;
  @Deprecated public java.lang.String location;
  @Deprecated public com.madhouse.ssp.avro.MediaRequest request;
  @Deprecated public com.madhouse.ssp.avro.MediaResponse response;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public MediaBid() {}

  /**
   * All-args constructor.
   * @param time The new value for time
   * @param ua The new value for ua
   * @param ip The new value for ip
   * @param impid The new value for impid
   * @param status The new value for status
   * @param bidfloor The new value for bidfloor
   * @param bidtype The new value for bidtype
   * @param location The new value for location
   * @param request The new value for request
   * @param response The new value for response
   */
  public MediaBid(java.lang.Long time, java.lang.String ua, java.lang.String ip, java.lang.String impid, java.lang.Integer status, java.lang.Integer bidfloor, java.lang.Integer bidtype, java.lang.String location, com.madhouse.ssp.avro.MediaRequest request, com.madhouse.ssp.avro.MediaResponse response) {
    this.time = time;
    this.ua = ua;
    this.ip = ip;
    this.impid = impid;
    this.status = status;
    this.bidfloor = bidfloor;
    this.bidtype = bidtype;
    this.location = location;
    this.request = request;
    this.response = response;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return time;
    case 1: return ua;
    case 2: return ip;
    case 3: return impid;
    case 4: return status;
    case 5: return bidfloor;
    case 6: return bidtype;
    case 7: return location;
    case 8: return request;
    case 9: return response;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: time = (java.lang.Long)value$; break;
    case 1: ua = (java.lang.String)value$; break;
    case 2: ip = (java.lang.String)value$; break;
    case 3: impid = (java.lang.String)value$; break;
    case 4: status = (java.lang.Integer)value$; break;
    case 5: bidfloor = (java.lang.Integer)value$; break;
    case 6: bidtype = (java.lang.Integer)value$; break;
    case 7: location = (java.lang.String)value$; break;
    case 8: request = (com.madhouse.ssp.avro.MediaRequest)value$; break;
    case 9: response = (com.madhouse.ssp.avro.MediaResponse)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'time' field.
   * @return The value of the 'time' field.
   */
  public java.lang.Long getTime() {
    return time;
  }

  /**
   * Sets the value of the 'time' field.
   * @param value the value to set.
   */
  public void setTime(java.lang.Long value) {
    this.time = value;
  }

  /**
   * Gets the value of the 'ua' field.
   * @return The value of the 'ua' field.
   */
  public java.lang.String getUa() {
    return ua;
  }

  /**
   * Sets the value of the 'ua' field.
   * @param value the value to set.
   */
  public void setUa(java.lang.String value) {
    this.ua = value;
  }

  /**
   * Gets the value of the 'ip' field.
   * @return The value of the 'ip' field.
   */
  public java.lang.String getIp() {
    return ip;
  }

  /**
   * Sets the value of the 'ip' field.
   * @param value the value to set.
   */
  public void setIp(java.lang.String value) {
    this.ip = value;
  }

  /**
   * Gets the value of the 'impid' field.
   * @return The value of the 'impid' field.
   */
  public java.lang.String getImpid() {
    return impid;
  }

  /**
   * Sets the value of the 'impid' field.
   * @param value the value to set.
   */
  public void setImpid(java.lang.String value) {
    this.impid = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public java.lang.Integer getStatus() {
    return status;
  }

  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(java.lang.Integer value) {
    this.status = value;
  }

  /**
   * Gets the value of the 'bidfloor' field.
   * @return The value of the 'bidfloor' field.
   */
  public java.lang.Integer getBidfloor() {
    return bidfloor;
  }

  /**
   * Sets the value of the 'bidfloor' field.
   * @param value the value to set.
   */
  public void setBidfloor(java.lang.Integer value) {
    this.bidfloor = value;
  }

  /**
   * Gets the value of the 'bidtype' field.
   * @return The value of the 'bidtype' field.
   */
  public java.lang.Integer getBidtype() {
    return bidtype;
  }

  /**
   * Sets the value of the 'bidtype' field.
   * @param value the value to set.
   */
  public void setBidtype(java.lang.Integer value) {
    this.bidtype = value;
  }

  /**
   * Gets the value of the 'location' field.
   * @return The value of the 'location' field.
   */
  public java.lang.String getLocation() {
    return location;
  }

  /**
   * Sets the value of the 'location' field.
   * @param value the value to set.
   */
  public void setLocation(java.lang.String value) {
    this.location = value;
  }

  /**
   * Gets the value of the 'request' field.
   * @return The value of the 'request' field.
   */
  public com.madhouse.ssp.avro.MediaRequest getRequest() {
    return request;
  }

  /**
   * Sets the value of the 'request' field.
   * @param value the value to set.
   */
  public void setRequest(com.madhouse.ssp.avro.MediaRequest value) {
    this.request = value;
  }

  /**
   * Gets the value of the 'response' field.
   * @return The value of the 'response' field.
   */
  public com.madhouse.ssp.avro.MediaResponse getResponse() {
    return response;
  }

  /**
   * Sets the value of the 'response' field.
   * @param value the value to set.
   */
  public void setResponse(com.madhouse.ssp.avro.MediaResponse value) {
    this.response = value;
  }

  /**
   * Creates a new MediaBid RecordBuilder.
   * @return A new MediaBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.MediaBid.Builder newBuilder() {
    return new com.madhouse.ssp.avro.MediaBid.Builder();
  }

  /**
   * Creates a new MediaBid RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new MediaBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.MediaBid.Builder newBuilder(com.madhouse.ssp.avro.MediaBid.Builder other) {
    return new com.madhouse.ssp.avro.MediaBid.Builder(other);
  }

  /**
   * Creates a new MediaBid RecordBuilder by copying an existing MediaBid instance.
   * @param other The existing instance to copy.
   * @return A new MediaBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.MediaBid.Builder newBuilder(com.madhouse.ssp.avro.MediaBid other) {
    return new com.madhouse.ssp.avro.MediaBid.Builder(other);
  }

  /**
   * RecordBuilder for MediaBid instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<MediaBid>
    implements org.apache.avro.data.RecordBuilder<MediaBid> {

    private long time;
    private java.lang.String ua;
    private java.lang.String ip;
    private java.lang.String impid;
    private int status;
    private int bidfloor;
    private int bidtype;
    private java.lang.String location;
    private com.madhouse.ssp.avro.MediaRequest request;
    private com.madhouse.ssp.avro.MediaRequest.Builder requestBuilder;
    private com.madhouse.ssp.avro.MediaResponse response;
    private com.madhouse.ssp.avro.MediaResponse.Builder responseBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.madhouse.ssp.avro.MediaBid.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.ua)) {
        this.ua = data().deepCopy(fields()[1].schema(), other.ua);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.ip)) {
        this.ip = data().deepCopy(fields()[2].schema(), other.ip);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.impid)) {
        this.impid = data().deepCopy(fields()[3].schema(), other.impid);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.status)) {
        this.status = data().deepCopy(fields()[4].schema(), other.status);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.bidfloor)) {
        this.bidfloor = data().deepCopy(fields()[5].schema(), other.bidfloor);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.bidtype)) {
        this.bidtype = data().deepCopy(fields()[6].schema(), other.bidtype);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.location)) {
        this.location = data().deepCopy(fields()[7].schema(), other.location);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.request)) {
        this.request = data().deepCopy(fields()[8].schema(), other.request);
        fieldSetFlags()[8] = true;
      }
      if (other.hasRequestBuilder()) {
        this.requestBuilder = com.madhouse.ssp.avro.MediaRequest.newBuilder(other.getRequestBuilder());
      }
      if (isValidValue(fields()[9], other.response)) {
        this.response = data().deepCopy(fields()[9].schema(), other.response);
        fieldSetFlags()[9] = true;
      }
      if (other.hasResponseBuilder()) {
        this.responseBuilder = com.madhouse.ssp.avro.MediaResponse.newBuilder(other.getResponseBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing MediaBid instance
     * @param other The existing instance to copy.
     */
    private Builder(com.madhouse.ssp.avro.MediaBid other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.ua)) {
        this.ua = data().deepCopy(fields()[1].schema(), other.ua);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.ip)) {
        this.ip = data().deepCopy(fields()[2].schema(), other.ip);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.impid)) {
        this.impid = data().deepCopy(fields()[3].schema(), other.impid);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.status)) {
        this.status = data().deepCopy(fields()[4].schema(), other.status);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.bidfloor)) {
        this.bidfloor = data().deepCopy(fields()[5].schema(), other.bidfloor);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.bidtype)) {
        this.bidtype = data().deepCopy(fields()[6].schema(), other.bidtype);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.location)) {
        this.location = data().deepCopy(fields()[7].schema(), other.location);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.request)) {
        this.request = data().deepCopy(fields()[8].schema(), other.request);
        fieldSetFlags()[8] = true;
      }
      this.requestBuilder = null;
      if (isValidValue(fields()[9], other.response)) {
        this.response = data().deepCopy(fields()[9].schema(), other.response);
        fieldSetFlags()[9] = true;
      }
      this.responseBuilder = null;
    }

    /**
      * Gets the value of the 'time' field.
      * @return The value.
      */
    public java.lang.Long getTime() {
      return time;
    }

    /**
      * Sets the value of the 'time' field.
      * @param value The value of 'time'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setTime(long value) {
      validate(fields()[0], value);
      this.time = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'time' field has been set.
      * @return True if the 'time' field has been set, false otherwise.
      */
    public boolean hasTime() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'time' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearTime() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'ua' field.
      * @return The value.
      */
    public java.lang.String getUa() {
      return ua;
    }

    /**
      * Sets the value of the 'ua' field.
      * @param value The value of 'ua'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setUa(java.lang.String value) {
      validate(fields()[1], value);
      this.ua = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'ua' field has been set.
      * @return True if the 'ua' field has been set, false otherwise.
      */
    public boolean hasUa() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'ua' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearUa() {
      ua = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'ip' field.
      * @return The value.
      */
    public java.lang.String getIp() {
      return ip;
    }

    /**
      * Sets the value of the 'ip' field.
      * @param value The value of 'ip'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setIp(java.lang.String value) {
      validate(fields()[2], value);
      this.ip = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'ip' field has been set.
      * @return True if the 'ip' field has been set, false otherwise.
      */
    public boolean hasIp() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'ip' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearIp() {
      ip = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'impid' field.
      * @return The value.
      */
    public java.lang.String getImpid() {
      return impid;
    }

    /**
      * Sets the value of the 'impid' field.
      * @param value The value of 'impid'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setImpid(java.lang.String value) {
      validate(fields()[3], value);
      this.impid = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'impid' field has been set.
      * @return True if the 'impid' field has been set, false otherwise.
      */
    public boolean hasImpid() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'impid' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearImpid() {
      impid = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public java.lang.Integer getStatus() {
      return status;
    }

    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setStatus(int value) {
      validate(fields()[4], value);
      this.status = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearStatus() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'bidfloor' field.
      * @return The value.
      */
    public java.lang.Integer getBidfloor() {
      return bidfloor;
    }

    /**
      * Sets the value of the 'bidfloor' field.
      * @param value The value of 'bidfloor'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setBidfloor(int value) {
      validate(fields()[5], value);
      this.bidfloor = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'bidfloor' field has been set.
      * @return True if the 'bidfloor' field has been set, false otherwise.
      */
    public boolean hasBidfloor() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'bidfloor' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearBidfloor() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'bidtype' field.
      * @return The value.
      */
    public java.lang.Integer getBidtype() {
      return bidtype;
    }

    /**
      * Sets the value of the 'bidtype' field.
      * @param value The value of 'bidtype'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setBidtype(int value) {
      validate(fields()[6], value);
      this.bidtype = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'bidtype' field has been set.
      * @return True if the 'bidtype' field has been set, false otherwise.
      */
    public boolean hasBidtype() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'bidtype' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearBidtype() {
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'location' field.
      * @return The value.
      */
    public java.lang.String getLocation() {
      return location;
    }

    /**
      * Sets the value of the 'location' field.
      * @param value The value of 'location'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setLocation(java.lang.String value) {
      validate(fields()[7], value);
      this.location = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'location' field has been set.
      * @return True if the 'location' field has been set, false otherwise.
      */
    public boolean hasLocation() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'location' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearLocation() {
      location = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'request' field.
      * @return The value.
      */
    public com.madhouse.ssp.avro.MediaRequest getRequest() {
      return request;
    }

    /**
      * Sets the value of the 'request' field.
      * @param value The value of 'request'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setRequest(com.madhouse.ssp.avro.MediaRequest value) {
      validate(fields()[8], value);
      this.requestBuilder = null;
      this.request = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'request' field has been set.
      * @return True if the 'request' field has been set, false otherwise.
      */
    public boolean hasRequest() {
      return fieldSetFlags()[8];
    }

    /**
     * Gets the Builder instance for the 'request' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.MediaRequest.Builder getRequestBuilder() {
      if (requestBuilder == null) {
        if (hasRequest()) {
          setRequestBuilder(com.madhouse.ssp.avro.MediaRequest.newBuilder(request));
        } else {
          setRequestBuilder(com.madhouse.ssp.avro.MediaRequest.newBuilder());
        }
      }
      return requestBuilder;
    }

    /**
     * Sets the Builder instance for the 'request' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.MediaBid.Builder setRequestBuilder(com.madhouse.ssp.avro.MediaRequest.Builder value) {
      clearRequest();
      requestBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'request' field has an active Builder instance
     * @return True if the 'request' field has an active Builder instance
     */
    public boolean hasRequestBuilder() {
      return requestBuilder != null;
    }

    /**
      * Clears the value of the 'request' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearRequest() {
      request = null;
      requestBuilder = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'response' field.
      * @return The value.
      */
    public com.madhouse.ssp.avro.MediaResponse getResponse() {
      return response;
    }

    /**
      * Sets the value of the 'response' field.
      * @param value The value of 'response'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder setResponse(com.madhouse.ssp.avro.MediaResponse value) {
      validate(fields()[9], value);
      this.responseBuilder = null;
      this.response = value;
      fieldSetFlags()[9] = true;
      return this;
    }

    /**
      * Checks whether the 'response' field has been set.
      * @return True if the 'response' field has been set, false otherwise.
      */
    public boolean hasResponse() {
      return fieldSetFlags()[9];
    }

    /**
     * Gets the Builder instance for the 'response' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.MediaResponse.Builder getResponseBuilder() {
      if (responseBuilder == null) {
        if (hasResponse()) {
          setResponseBuilder(com.madhouse.ssp.avro.MediaResponse.newBuilder(response));
        } else {
          setResponseBuilder(com.madhouse.ssp.avro.MediaResponse.newBuilder());
        }
      }
      return responseBuilder;
    }

    /**
     * Sets the Builder instance for the 'response' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.MediaBid.Builder setResponseBuilder(com.madhouse.ssp.avro.MediaResponse.Builder value) {
      clearResponse();
      responseBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'response' field has an active Builder instance
     * @return True if the 'response' field has an active Builder instance
     */
    public boolean hasResponseBuilder() {
      return responseBuilder != null;
    }

    /**
      * Clears the value of the 'response' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.MediaBid.Builder clearResponse() {
      response = null;
      responseBuilder = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MediaBid build() {
      try {
        MediaBid record = new MediaBid();
        record.time = fieldSetFlags()[0] ? this.time : (java.lang.Long) defaultValue(fields()[0]);
        record.ua = fieldSetFlags()[1] ? this.ua : (java.lang.String) defaultValue(fields()[1]);
        record.ip = fieldSetFlags()[2] ? this.ip : (java.lang.String) defaultValue(fields()[2]);
        record.impid = fieldSetFlags()[3] ? this.impid : (java.lang.String) defaultValue(fields()[3]);
        record.status = fieldSetFlags()[4] ? this.status : (java.lang.Integer) defaultValue(fields()[4]);
        record.bidfloor = fieldSetFlags()[5] ? this.bidfloor : (java.lang.Integer) defaultValue(fields()[5]);
        record.bidtype = fieldSetFlags()[6] ? this.bidtype : (java.lang.Integer) defaultValue(fields()[6]);
        record.location = fieldSetFlags()[7] ? this.location : (java.lang.String) defaultValue(fields()[7]);
        if (requestBuilder != null) {
          record.request = this.requestBuilder.build();
        } else {
          record.request = fieldSetFlags()[8] ? this.request : (com.madhouse.ssp.avro.MediaRequest) defaultValue(fields()[8]);
        }
        if (responseBuilder != null) {
          record.response = this.responseBuilder.build();
        } else {
          record.response = fieldSetFlags()[9] ? this.response : (com.madhouse.ssp.avro.MediaResponse) defaultValue(fields()[9]);
        }
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<MediaBid>
    WRITER$ = (org.apache.avro.io.DatumWriter<MediaBid>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<MediaBid>
    READER$ = (org.apache.avro.io.DatumReader<MediaBid>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
