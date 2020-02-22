package xyz.tomd.cameldemos.springboot.cxf;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "content"
})
@XmlRootElement(name = "GetAllBooksResponse")
public class GetAllBooksResponse {

    @XmlElement(name = "content")
    protected String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
