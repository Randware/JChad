package net.jchad.server.model.command.commands.about;

public class Asset {
    private String url;
    private long id;
    private String node_id;
    private String name;
    private String content_type;
    private String state;
    private long size;
    private String download_count;
    private String created_at;
    private String uploaded_at;
    private String browser_download_url;
    private Author uploader;

    public Asset(String url, long id, String node_id,
                 String name, String content_type, String state, long size, String download_count, String created_at,
                 String uploaded_at, String browser_download_url, Author uploader) {
        this.url = url;
        this.id = id;
        this.node_id = node_id;
        this.name = name;
        this.content_type = content_type;
        this.state = state;
        this.size = size;
        this.download_count = download_count;
        this.created_at = created_at;
        this.uploaded_at = uploaded_at;
        this.browser_download_url = browser_download_url;
        this.uploader = uploader;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownload_count() {
        return download_count;
    }

    public void setDownload_count(String download_count) {
        this.download_count = download_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(String uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getBrowser_download_url() {
        return browser_download_url;
    }

    public void setBrowser_download_url(String browser_download_url) {
        this.browser_download_url = browser_download_url;
    }

    public Author getUploader() {
        return uploader;
    }

    public void setUploader(Author uploader) {
        this.uploader = uploader;
    }
}
