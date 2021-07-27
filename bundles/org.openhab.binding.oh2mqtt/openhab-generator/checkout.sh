
git init
git remote add -f github git@github.com:nexiles/openhab-addons.git

rm -f pom.xml
rm -rf bundles/
rm -rf tools/

git checkout "github/main" -- pom.xml bundles/archetype-settings.xml bundles/create_openhab_binding_skeleton.cmd bundles/create_openhab_binding_skeleton.sh bundles/pom.xml tools

rm -rf .git/
