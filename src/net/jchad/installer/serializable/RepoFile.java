package net.jchad.installer.serializable;

public record RepoFile(String url,
                       long id,
                       String name,
                       long size,
                       int download_count,
                       String browser_download_url,
                       RepoUploader uploader
) {


    @Override
    public String toString() {
        return "RepoFile{" +
                "url='" + url + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", download_count=" + download_count +
                ", browser_download_url='" + browser_download_url + '\'' +
                ", uploader=" + uploader +
                '}';
    }
}
