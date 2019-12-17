FROM ubuntu:latest

# Install compiler and perl stuff
RUN apt-get update && \
    apt-get install --yes \
      build-essential \
      gcc-multilib \
      perl cpanminus

RUN cpanm Lingua::Jspell::EAGLES

ENV LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
ENV PATH=/usr/local/bin:$PATH

WORKDIR /


