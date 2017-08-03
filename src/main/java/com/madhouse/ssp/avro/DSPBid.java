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
public class DSPBid extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2527205341039149203L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DSPBid\",\"namespace\":\"com.madhouse.ssp.avro\",\"fields\":[{\"name\":\"time\",\"type\":\"long\"},{\"name\":\"dspid\",\"type\":\"long\"},{\"name\":\"policyid\",\"type\":\"long\"},{\"name\":\"deliverytype\",\"type\":\"int\"},{\"name\":\"mediaid\",\"type\":\"long\"},{\"name\":\"adspaceid\",\"type\":\"long\"},{\"name\":\"status\",\"type\":\"int\"},{\"name\":\"winner\",\"type\":\"int\",\"default\":0},{\"name\":\"request\",\"type\":{\"type\":\"record\",\"name\":\"DSPRequest\",\"fields\":[{\"name\":\"id\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"impid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"adtype\",\"type\":\"int\"},{\"name\":\"layout\",\"type\":\"int\"},{\"name\":\"tagid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"dealid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"test\",\"type\":\"int\"},{\"name\":\"bidfloor\",\"type\":\"int\",\"default\":0},{\"name\":\"bidtype\",\"type\":\"int\",\"default\":1},{\"name\":\"tmax\",\"type\":\"int\",\"default\":120}]}},{\"name\":\"response\",\"type\":{\"type\":\"record\",\"name\":\"DSPResponse\",\"fields\":[{\"name\":\"id\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"bidid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"impid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"adid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"cid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"crid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"price\",\"type\":\"int\",\"default\":0},{\"name\":\"nurl\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"admid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"icon\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"cover\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"title\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"desc\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"duration\",\"type\":\"int\",\"default\":0},{\"name\":\"adm\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"dealid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"lpgurl\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"acttype\",\"type\":\"int\",\"default\":1},{\"name\":\"monitor\",\"type\":{\"type\":\"record\",\"name\":\"Monitor\",\"fields\":[{\"name\":\"impurl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Track\",\"fields\":[{\"name\":\"startdelay\",\"type\":\"int\",\"default\":0},{\"name\":\"url\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}}},{\"name\":\"clkurl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"securl\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"exts\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}}]}}]},\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<DSPBid> ENCODER =
      new BinaryMessageEncoder<DSPBid>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<DSPBid> DECODER =
      new BinaryMessageDecoder<DSPBid>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<DSPBid> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<DSPBid> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<DSPBid>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this DSPBid to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a DSPBid from a ByteBuffer. */
  public static DSPBid fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public long time;
  @Deprecated public long dspid;
  @Deprecated public long policyid;
  @Deprecated public int deliverytype;
  @Deprecated public long mediaid;
  @Deprecated public long adspaceid;
  @Deprecated public int status;
  @Deprecated public int winner;
  @Deprecated public com.madhouse.ssp.avro.DSPRequest request;
  @Deprecated public com.madhouse.ssp.avro.DSPResponse response;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public DSPBid() {}

  /**
   * All-args constructor.
   * @param time The new value for time
   * @param dspid The new value for dspid
   * @param policyid The new value for policyid
   * @param deliverytype The new value for deliverytype
   * @param mediaid The new value for mediaid
   * @param adspaceid The new value for adspaceid
   * @param status The new value for status
   * @param winner The new value for winner
   * @param request The new value for request
   * @param response The new value for response
   */
  public DSPBid(java.lang.Long time, java.lang.Long dspid, java.lang.Long policyid, java.lang.Integer deliverytype, java.lang.Long mediaid, java.lang.Long adspaceid, java.lang.Integer status, java.lang.Integer winner, com.madhouse.ssp.avro.DSPRequest request, com.madhouse.ssp.avro.DSPResponse response) {
    this.time = time;
    this.dspid = dspid;
    this.policyid = policyid;
    this.deliverytype = deliverytype;
    this.mediaid = mediaid;
    this.adspaceid = adspaceid;
    this.status = status;
    this.winner = winner;
    this.request = request;
    this.response = response;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return time;
    case 1: return dspid;
    case 2: return policyid;
    case 3: return deliverytype;
    case 4: return mediaid;
    case 5: return adspaceid;
    case 6: return status;
    case 7: return winner;
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
    case 1: dspid = (java.lang.Long)value$; break;
    case 2: policyid = (java.lang.Long)value$; break;
    case 3: deliverytype = (java.lang.Integer)value$; break;
    case 4: mediaid = (java.lang.Long)value$; break;
    case 5: adspaceid = (java.lang.Long)value$; break;
    case 6: status = (java.lang.Integer)value$; break;
    case 7: winner = (java.lang.Integer)value$; break;
    case 8: request = (com.madhouse.ssp.avro.DSPRequest)value$; break;
    case 9: response = (com.madhouse.ssp.avro.DSPResponse)value$; break;
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
   * Gets the value of the 'dspid' field.
   * @return The value of the 'dspid' field.
   */
  public java.lang.Long getDspid() {
    return dspid;
  }

  /**
   * Sets the value of the 'dspid' field.
   * @param value the value to set.
   */
  public void setDspid(java.lang.Long value) {
    this.dspid = value;
  }

  /**
   * Gets the value of the 'policyid' field.
   * @return The value of the 'policyid' field.
   */
  public java.lang.Long getPolicyid() {
    return policyid;
  }

  /**
   * Sets the value of the 'policyid' field.
   * @param value the value to set.
   */
  public void setPolicyid(java.lang.Long value) {
    this.policyid = value;
  }

  /**
   * Gets the value of the 'deliverytype' field.
   * @return The value of the 'deliverytype' field.
   */
  public java.lang.Integer getDeliverytype() {
    return deliverytype;
  }

  /**
   * Sets the value of the 'deliverytype' field.
   * @param value the value to set.
   */
  public void setDeliverytype(java.lang.Integer value) {
    this.deliverytype = value;
  }

  /**
   * Gets the value of the 'mediaid' field.
   * @return The value of the 'mediaid' field.
   */
  public java.lang.Long getMediaid() {
    return mediaid;
  }

  /**
   * Sets the value of the 'mediaid' field.
   * @param value the value to set.
   */
  public void setMediaid(java.lang.Long value) {
    this.mediaid = value;
  }

  /**
   * Gets the value of the 'adspaceid' field.
   * @return The value of the 'adspaceid' field.
   */
  public java.lang.Long getAdspaceid() {
    return adspaceid;
  }

  /**
   * Sets the value of the 'adspaceid' field.
   * @param value the value to set.
   */
  public void setAdspaceid(java.lang.Long value) {
    this.adspaceid = value;
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
   * Gets the value of the 'winner' field.
   * @return The value of the 'winner' field.
   */
  public java.lang.Integer getWinner() {
    return winner;
  }

  /**
   * Sets the value of the 'winner' field.
   * @param value the value to set.
   */
  public void setWinner(java.lang.Integer value) {
    this.winner = value;
  }

  /**
   * Gets the value of the 'request' field.
   * @return The value of the 'request' field.
   */
  public com.madhouse.ssp.avro.DSPRequest getRequest() {
    return request;
  }

  /**
   * Sets the value of the 'request' field.
   * @param value the value to set.
   */
  public void setRequest(com.madhouse.ssp.avro.DSPRequest value) {
    this.request = value;
  }

  /**
   * Gets the value of the 'response' field.
   * @return The value of the 'response' field.
   */
  public com.madhouse.ssp.avro.DSPResponse getResponse() {
    return response;
  }

  /**
   * Sets the value of the 'response' field.
   * @param value the value to set.
   */
  public void setResponse(com.madhouse.ssp.avro.DSPResponse value) {
    this.response = value;
  }

  /**
   * Creates a new DSPBid RecordBuilder.
   * @return A new DSPBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.DSPBid.Builder newBuilder() {
    return new com.madhouse.ssp.avro.DSPBid.Builder();
  }

  /**
   * Creates a new DSPBid RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new DSPBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.DSPBid.Builder newBuilder(com.madhouse.ssp.avro.DSPBid.Builder other) {
    return new com.madhouse.ssp.avro.DSPBid.Builder(other);
  }

  /**
   * Creates a new DSPBid RecordBuilder by copying an existing DSPBid instance.
   * @param other The existing instance to copy.
   * @return A new DSPBid RecordBuilder
   */
  public static com.madhouse.ssp.avro.DSPBid.Builder newBuilder(com.madhouse.ssp.avro.DSPBid other) {
    return new com.madhouse.ssp.avro.DSPBid.Builder(other);
  }

  /**
   * RecordBuilder for DSPBid instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DSPBid>
    implements org.apache.avro.data.RecordBuilder<DSPBid> {

    private long time;
    private long dspid;
    private long policyid;
    private int deliverytype;
    private long mediaid;
    private long adspaceid;
    private int status;
    private int winner;
    private com.madhouse.ssp.avro.DSPRequest request;
    private com.madhouse.ssp.avro.DSPRequest.Builder requestBuilder;
    private com.madhouse.ssp.avro.DSPResponse response;
    private com.madhouse.ssp.avro.DSPResponse.Builder responseBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.madhouse.ssp.avro.DSPBid.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.dspid)) {
        this.dspid = data().deepCopy(fields()[1].schema(), other.dspid);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.policyid)) {
        this.policyid = data().deepCopy(fields()[2].schema(), other.policyid);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.deliverytype)) {
        this.deliverytype = data().deepCopy(fields()[3].schema(), other.deliverytype);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.mediaid)) {
        this.mediaid = data().deepCopy(fields()[4].schema(), other.mediaid);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.adspaceid)) {
        this.adspaceid = data().deepCopy(fields()[5].schema(), other.adspaceid);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.status)) {
        this.status = data().deepCopy(fields()[6].schema(), other.status);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.winner)) {
        this.winner = data().deepCopy(fields()[7].schema(), other.winner);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.request)) {
        this.request = data().deepCopy(fields()[8].schema(), other.request);
        fieldSetFlags()[8] = true;
      }
      if (other.hasRequestBuilder()) {
        this.requestBuilder = com.madhouse.ssp.avro.DSPRequest.newBuilder(other.getRequestBuilder());
      }
      if (isValidValue(fields()[9], other.response)) {
        this.response = data().deepCopy(fields()[9].schema(), other.response);
        fieldSetFlags()[9] = true;
      }
      if (other.hasResponseBuilder()) {
        this.responseBuilder = com.madhouse.ssp.avro.DSPResponse.newBuilder(other.getResponseBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing DSPBid instance
     * @param other The existing instance to copy.
     */
    private Builder(com.madhouse.ssp.avro.DSPBid other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.time)) {
        this.time = data().deepCopy(fields()[0].schema(), other.time);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.dspid)) {
        this.dspid = data().deepCopy(fields()[1].schema(), other.dspid);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.policyid)) {
        this.policyid = data().deepCopy(fields()[2].schema(), other.policyid);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.deliverytype)) {
        this.deliverytype = data().deepCopy(fields()[3].schema(), other.deliverytype);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.mediaid)) {
        this.mediaid = data().deepCopy(fields()[4].schema(), other.mediaid);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.adspaceid)) {
        this.adspaceid = data().deepCopy(fields()[5].schema(), other.adspaceid);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.status)) {
        this.status = data().deepCopy(fields()[6].schema(), other.status);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.winner)) {
        this.winner = data().deepCopy(fields()[7].schema(), other.winner);
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
    public com.madhouse.ssp.avro.DSPBid.Builder setTime(long value) {
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
    public com.madhouse.ssp.avro.DSPBid.Builder clearTime() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'dspid' field.
      * @return The value.
      */
    public java.lang.Long getDspid() {
      return dspid;
    }

    /**
      * Sets the value of the 'dspid' field.
      * @param value The value of 'dspid'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setDspid(long value) {
      validate(fields()[1], value);
      this.dspid = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'dspid' field has been set.
      * @return True if the 'dspid' field has been set, false otherwise.
      */
    public boolean hasDspid() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'dspid' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearDspid() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'policyid' field.
      * @return The value.
      */
    public java.lang.Long getPolicyid() {
      return policyid;
    }

    /**
      * Sets the value of the 'policyid' field.
      * @param value The value of 'policyid'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setPolicyid(long value) {
      validate(fields()[2], value);
      this.policyid = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'policyid' field has been set.
      * @return True if the 'policyid' field has been set, false otherwise.
      */
    public boolean hasPolicyid() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'policyid' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearPolicyid() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'deliverytype' field.
      * @return The value.
      */
    public java.lang.Integer getDeliverytype() {
      return deliverytype;
    }

    /**
      * Sets the value of the 'deliverytype' field.
      * @param value The value of 'deliverytype'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setDeliverytype(int value) {
      validate(fields()[3], value);
      this.deliverytype = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'deliverytype' field has been set.
      * @return True if the 'deliverytype' field has been set, false otherwise.
      */
    public boolean hasDeliverytype() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'deliverytype' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearDeliverytype() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'mediaid' field.
      * @return The value.
      */
    public java.lang.Long getMediaid() {
      return mediaid;
    }

    /**
      * Sets the value of the 'mediaid' field.
      * @param value The value of 'mediaid'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setMediaid(long value) {
      validate(fields()[4], value);
      this.mediaid = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'mediaid' field has been set.
      * @return True if the 'mediaid' field has been set, false otherwise.
      */
    public boolean hasMediaid() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'mediaid' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearMediaid() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'adspaceid' field.
      * @return The value.
      */
    public java.lang.Long getAdspaceid() {
      return adspaceid;
    }

    /**
      * Sets the value of the 'adspaceid' field.
      * @param value The value of 'adspaceid'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setAdspaceid(long value) {
      validate(fields()[5], value);
      this.adspaceid = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'adspaceid' field has been set.
      * @return True if the 'adspaceid' field has been set, false otherwise.
      */
    public boolean hasAdspaceid() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'adspaceid' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearAdspaceid() {
      fieldSetFlags()[5] = false;
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
    public com.madhouse.ssp.avro.DSPBid.Builder setStatus(int value) {
      validate(fields()[6], value);
      this.status = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearStatus() {
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'winner' field.
      * @return The value.
      */
    public java.lang.Integer getWinner() {
      return winner;
    }

    /**
      * Sets the value of the 'winner' field.
      * @param value The value of 'winner'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setWinner(int value) {
      validate(fields()[7], value);
      this.winner = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'winner' field has been set.
      * @return True if the 'winner' field has been set, false otherwise.
      */
    public boolean hasWinner() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'winner' field.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder clearWinner() {
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'request' field.
      * @return The value.
      */
    public com.madhouse.ssp.avro.DSPRequest getRequest() {
      return request;
    }

    /**
      * Sets the value of the 'request' field.
      * @param value The value of 'request'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setRequest(com.madhouse.ssp.avro.DSPRequest value) {
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
    public com.madhouse.ssp.avro.DSPRequest.Builder getRequestBuilder() {
      if (requestBuilder == null) {
        if (hasRequest()) {
          setRequestBuilder(com.madhouse.ssp.avro.DSPRequest.newBuilder(request));
        } else {
          setRequestBuilder(com.madhouse.ssp.avro.DSPRequest.newBuilder());
        }
      }
      return requestBuilder;
    }

    /**
     * Sets the Builder instance for the 'request' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.DSPBid.Builder setRequestBuilder(com.madhouse.ssp.avro.DSPRequest.Builder value) {
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
    public com.madhouse.ssp.avro.DSPBid.Builder clearRequest() {
      request = null;
      requestBuilder = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'response' field.
      * @return The value.
      */
    public com.madhouse.ssp.avro.DSPResponse getResponse() {
      return response;
    }

    /**
      * Sets the value of the 'response' field.
      * @param value The value of 'response'.
      * @return This builder.
      */
    public com.madhouse.ssp.avro.DSPBid.Builder setResponse(com.madhouse.ssp.avro.DSPResponse value) {
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
    public com.madhouse.ssp.avro.DSPResponse.Builder getResponseBuilder() {
      if (responseBuilder == null) {
        if (hasResponse()) {
          setResponseBuilder(com.madhouse.ssp.avro.DSPResponse.newBuilder(response));
        } else {
          setResponseBuilder(com.madhouse.ssp.avro.DSPResponse.newBuilder());
        }
      }
      return responseBuilder;
    }

    /**
     * Sets the Builder instance for the 'response' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public com.madhouse.ssp.avro.DSPBid.Builder setResponseBuilder(com.madhouse.ssp.avro.DSPResponse.Builder value) {
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
    public com.madhouse.ssp.avro.DSPBid.Builder clearResponse() {
      response = null;
      responseBuilder = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DSPBid build() {
      try {
        DSPBid record = new DSPBid();
        record.time = fieldSetFlags()[0] ? this.time : (java.lang.Long) defaultValue(fields()[0]);
        record.dspid = fieldSetFlags()[1] ? this.dspid : (java.lang.Long) defaultValue(fields()[1]);
        record.policyid = fieldSetFlags()[2] ? this.policyid : (java.lang.Long) defaultValue(fields()[2]);
        record.deliverytype = fieldSetFlags()[3] ? this.deliverytype : (java.lang.Integer) defaultValue(fields()[3]);
        record.mediaid = fieldSetFlags()[4] ? this.mediaid : (java.lang.Long) defaultValue(fields()[4]);
        record.adspaceid = fieldSetFlags()[5] ? this.adspaceid : (java.lang.Long) defaultValue(fields()[5]);
        record.status = fieldSetFlags()[6] ? this.status : (java.lang.Integer) defaultValue(fields()[6]);
        record.winner = fieldSetFlags()[7] ? this.winner : (java.lang.Integer) defaultValue(fields()[7]);
        if (requestBuilder != null) {
          record.request = this.requestBuilder.build();
        } else {
          record.request = fieldSetFlags()[8] ? this.request : (com.madhouse.ssp.avro.DSPRequest) defaultValue(fields()[8]);
        }
        if (responseBuilder != null) {
          record.response = this.responseBuilder.build();
        } else {
          record.response = fieldSetFlags()[9] ? this.response : (com.madhouse.ssp.avro.DSPResponse) defaultValue(fields()[9]);
        }
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<DSPBid>
    WRITER$ = (org.apache.avro.io.DatumWriter<DSPBid>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<DSPBid>
    READER$ = (org.apache.avro.io.DatumReader<DSPBid>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
