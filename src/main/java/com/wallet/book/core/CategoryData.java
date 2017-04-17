package com.wallet.book.core;

import com.wallet.utils.misc.Serializer;

import java.io.*;

/**
 * Created by neo on 4/6/17.
 */
public class CategoryData implements Serializable, Serializer<CategoryData> {
    static final long serialVersionUID = -5378184041612946692L;

    private String picture_id;

    public CategoryData(String picture_id) {
        this.setPicture_id(picture_id);
    }

    public CategoryData(InputStream in) throws IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("InputStream is null when build CategoryData");
        }
        ObjectInputStream ois = new ObjectInputStream(in);
        CategoryData categoryData = this.deserialize(ois);

        this.setPicture_id(categoryData.getPicture_id());
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(CategoryData categoryData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(categoryData);
        oos.close();
    }

    @Override
    public CategoryData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        CategoryData categoryData = (CategoryData)ois.readObject();
        ois.close();
        return categoryData;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }
}
