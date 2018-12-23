package api.base;

import lombok.Data;

@Data
public class ImageInfo {
    public byte[] rgbData;
    public int width;
    public int height;
}