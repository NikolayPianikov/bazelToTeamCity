pushd sample
bazel build :ProjectRunner --build_event_json_file out.json
popd