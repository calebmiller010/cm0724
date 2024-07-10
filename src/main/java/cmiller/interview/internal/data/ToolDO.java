package cmiller.interview.internal.data;

import org.apache.commons.lang3.StringUtils;

import cmiller.interview.common.Tool;

public class ToolDO implements Tool {
    @Override
    public String toString() {
	return "ToolDO [code=" + code + ", type=" + type + ", brand=" + brand + "]";
    }

    private final String code;
    private final Tool.Type type;
    private final String brand;

    public ToolDO(Builder builder) {
	this.code = builder.code;
	this.type = builder.type;
	this.brand = builder.brand;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public Tool.Type getType() {
	return type;
    }

    @Override
    public String getBrand() {
	return brand;
    }

    public static class Builder {
	private String code;
	private Tool.Type type;
	private String brand;

	public Builder code(String code) {
	    if (StringUtils.isBlank(code)) {
		throw new IllegalArgumentException("Tool.code cannot be null/blank");
	    }
	    this.code = code;
	    return this;
	}

	public Builder type(Tool.Type type) {
	    if (type == null) {
		throw new IllegalArgumentException("Tool.type cannot be null/blank");
	    }
	    this.type = type;
	    return this;
	}

	public Builder brand(String brand) {
	    if (StringUtils.isBlank(brand)) {
		throw new IllegalArgumentException("Tool.brand cannot be null/blank");
	    }
	    this.brand = brand;
	    return this;
	}

	public Tool build() {
	    return new ToolDO(this);
	}
    }
}
