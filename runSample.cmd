pushd sample
bazel build :ProjectRunner --bes_backend=localhost:54321
rem bazel build :ProjectRunner --build_event_json_file out.json
rem bazel shutdown
popd