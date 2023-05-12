package it.uniroma3.siw.model;

import java.util.Base64;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class Picture {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private byte[] data;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getImgData() {
        return Base64.getMimeEncoder().encodeToString(data);
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Picture))
			return false;
		Picture other = (Picture) obj;
		return Objects.equals(name, other.name);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("Picture size-> ");
		s.append(this.data.length);
		return s.toString();
	}
}
