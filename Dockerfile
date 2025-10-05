FROM --platform=linux/amd64 ubuntu:20.04


# Evitar preguntas interactivas en la instalación
ENV DEBIAN_FRONTEND=noninteractive

# Habilitar arquitectura i386 y actualizar repos
RUN dpkg --add-architecture i386 && apt-get update

# Instalar dependencias (64 y 32 bits necesarias para COOL)
RUN apt-get install -y \
    flex bison build-essential csh openjdk-11-jdk libxaw7-dev wget \
    libc6:i386 libncurses5:i386 libstdc++6:i386 \
    && rm -rf /var/lib/apt/lists/*

# Crear directorio para COOL
RUN mkdir -p /usr/class/cool
WORKDIR /usr/class/cool

# Descargar y extraer student-dist
RUN wget --no-check-certificate \
    'https://docs.google.com/uc?export=download&id=1Hfxe2c5aqjBs7P9qn0t1rGiMRGX5VBiA' \
    -O student-dist.tar.gz && \
    tar -xf student-dist.tar.gz && \
    rm student-dist.tar.gz

# Agregar COOL al PATH
ENV PATH="/usr/class/cool/bin:${PATH}"

# Crear symlink en el home del contenedor
RUN ln -s /usr/class/cool ~/cool

# Directorio de trabajo (se montará tu proyecto aquí)
WORKDIR /workspace

