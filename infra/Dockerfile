# Use the official Seata image as the base
FROM apache/seata-server:2.3.0

# Install dependencies for etcd
RUN apt-get update && apt-get install -y curl tar supervisor && rm -rf /var/lib/apt/lists/*

# Download and install etcd
ENV ETCD_VERSION=3.5.15
RUN curl -L https://github.com/etcd-io/etcd/releases/download/v${ETCD_VERSION}/etcd-v${ETCD_VERSION}-linux-amd64.tar.gz -o etcd.tar.gz \
    && tar xzvf etcd.tar.gz \
    && mv etcd-v${ETCD_VERSION}-linux-amd64/etcd /usr/local/bin/ \
    && mv etcd-v${ETCD_VERSION}-linux-amd64/etcdctl /usr/local/bin/ \
    && rm -rf etcd.tar.gz etcd-v${ETCD_VERSION}-linux-amd64

# Configure supervisord
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose ports for Seata and etcd
EXPOSE 2379 7091 8091

# Start supervisord to manage both processes
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]