FROM minio/minio:RELEASE.2022-06-07T00-33-41Z
#https://hub.docker.com/r/minio/minio
MAINTAINER smartcnpf@cnpf.fr
ENV TZ=Europe/Paris

CMD ["minio", "server", "--console-address", ":9001", "/data"]

