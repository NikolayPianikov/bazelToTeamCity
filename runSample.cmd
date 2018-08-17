pushd sample
bazel build :ProjectRunner --bes_backend=localhost:54321
rem bazel shutdown
popd