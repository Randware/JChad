package net.jchad.shared.common;

public class Author {
    private String login;
    private String node_id;
    private long id;
    private String url;
    private String html_url;
    private String repos_url;
    private String organizations_url;


    public Author(String login, String node_id, long id, String url, String html_url, String repos_url, String organizations_url) {
        this.login = login;
        this.node_id = node_id;
        this.id = id;
        this.url = url;
        this.html_url = html_url;
        this.repos_url = repos_url;
        this.organizations_url = organizations_url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getOrganizations_url() {
        return organizations_url;
    }

    public void setOrganizations_url(String organizations_url) {
        this.organizations_url = organizations_url;
    }
}
