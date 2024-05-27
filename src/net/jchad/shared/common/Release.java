package net.jchad.shared.common;

public class Release {
    private String name;
    private String body;
    private long id;
    private String tag_name;
    private Author author;
    private String tarball_url;
    private String zipball_url;
    private String node_id;
    private boolean draft;
    private boolean prerelease;
    private String target_comitish;
    private String created_at;
    private String published_at;
    private Asset[] assets;

    public Release(String name, String body, long id,
                   String tag_name, Author author, String tarball_url, String zipball_url, String node_id,
                   boolean draft, boolean prerelease, String target_comitish,
                   String created_at, String published_at, Asset[] assets) {
        this.name = name;
        this.body = body;
        this.id = id;
        this.tag_name = tag_name;
        this.author = author;
        this.tarball_url = tarball_url;
        this.zipball_url = zipball_url;
        this.node_id = node_id;
        this.draft = draft;
        this.prerelease = prerelease;
        this.target_comitish = target_comitish;
        this.created_at = created_at;
        this.published_at = published_at;
        this.assets = assets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTarball_url() {
        return tarball_url;
    }

    public void setTarball_url(String tarball_url) {
        this.tarball_url = tarball_url;
    }

    public String getZipball_url() {
        return zipball_url;
    }

    public void setZipball_url(String zipball_url) {
        this.zipball_url = zipball_url;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getTarget_comitish() {
        return target_comitish;
    }

    public void setTarget_comitish(String target_comitish) {
        this.target_comitish = target_comitish;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public Asset[] getAssets() {
        return assets;
    }

    public void setAssets(Asset[] assets) {
        this.assets = assets;
    }
}
