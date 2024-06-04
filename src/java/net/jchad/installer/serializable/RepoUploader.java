package net.jchad.installer.serializable;

public record RepoUploader(
                            String login,
                            long id,
                            String url
) {
    @Override
    public String toString() {
        return "RepoUploader{" +
                "login='" + login + '\'' +
                ", id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
