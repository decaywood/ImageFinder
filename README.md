# ImageFinder
A Image Search Engine
=============================
基于内容的图像搜索引擎，采用forkjoin框架实现并发IO、特征提取，充分利用CPU资源（IO为瓶颈）
基于策略模式将特征提取算法进行抽象，框架可以逐步拓展算法，进一步的多种算法可以协同使用，并
设置权重，提高图像搜索的准确率。
