# CUDA toolkit (nvcc and friends) from the image.
export PATH=/usr/local/cuda-11.4/bin/:/usr/src/tensorrt/bin/:$PATH
export LD_LIBRARY_PATH=/usr/local/cuda-11.4/lib64${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}
