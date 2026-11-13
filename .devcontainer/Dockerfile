FROM mcr.microsoft.com/devcontainers/java:25-bookworm

ARG JBR_VERSION
ARG MAVEN_VERSION
ARG HOTSWAP_AGENT_VERSION

ENV SDKMAN_DIR=/home/vscode/.sdkman

USER root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates \
       chromium \
       chromium-driver \
       curl \
       zip \
       unzip \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p "${SDKMAN_DIR}" \
    && chown -R vscode:vscode "${SDKMAN_DIR}"

USER vscode

RUN if [ ! -s "${SDKMAN_DIR}/bin/sdkman-init.sh" ]; then \
      curl -fsSL "https://get.sdkman.io" | bash; \
    fi

RUN test -n "${JBR_VERSION}" \
    && test -n "${MAVEN_VERSION}" \
    && sed -i 's/^sdkman_auto_answer=.*/sdkman_auto_answer=true/' \
       "${SDKMAN_DIR}/etc/config" \
    && sed -i 's/^sdkman_auto_selfupdate=.*/sdkman_auto_selfupdate=false/' \
       "${SDKMAN_DIR}/etc/config" \
    && bash -lc '\
      source "${SDKMAN_DIR}/bin/sdkman-init.sh" && \
      sdk install java "${JBR_VERSION}" && \
      sdk default java "${JBR_VERSION}" && \
      sdk install maven "${MAVEN_VERSION}" && \
      sdk default maven "${MAVEN_VERSION}" \
    '

ENV JAVA_HOME=/home/vscode/.sdkman/candidates/java/current
ENV MAVEN_HOME=/home/vscode/.sdkman/candidates/maven/current
ENV PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"